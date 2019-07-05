package com.miaoshaproject.miaosha.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.miaosha.controller.viewobject.UserVO;
import com.miaoshaproject.miaosha.error.BusinessException;
import com.miaoshaproject.miaosha.error.EnBusinessError;
import com.miaoshaproject.miaosha.response.CommonReturnType;
import com.miaoshaproject.miaosha.service.UserService;
import com.miaoshaproject.miaosha.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Author: 陈祥煜
 * Date: 2019-05-24 19:17
 * Description:
 *  @CrossOrigin: 解决Ajax跨域操作
 *  Druid是 JAVA语言中最好的数据库连接池
 */
@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController{
    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BusinessException {
        // 调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);
        return covertFromModel(userModel);
    }

    /**
     * 将核心领域模型对象转换为可供UI使用的viewobject
     * @param userModel
     * @return
     */
    public CommonReturnType covertFromModel(UserModel userModel) throws BusinessException {
        if (userModel == null){
            throw new BusinessException(EnBusinessError.USER_NOT_EXITS);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO);

        return CommonReturnType.create(userVO);
    }

    /**
     *   用户获取OTP短信接口
     *   @return
     */
    @RequestMapping(value = "/getOtp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FROMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telephone")String telephone){

        // 按照一定的规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(8999);
        randomInt += 1000;
        String otpCode = String.valueOf(randomInt);

        // 将OTP验证码同对应用户的手机号关联,本来应该使用Redis（用于分布式系统），但是使用了HttpSession
        this.httpServletRequest.getSession().setAttribute(telephone, otpCode);


        // 将OTP验证码通过短信通道发送给用户，省略
        System.out.println("telephone =" + telephone + " & otpCode =" +otpCode);
        return CommonReturnType.create(null);
    }

    @RequestMapping("/otp")
    public String getOtp(){
        return "getotp";
    }

    @RequestMapping("/success")
    public String success(){
        return "success";
    }

    @RequestMapping("/registerPage")
    public String register(){
        return "register";
    }
    @RequestMapping("/loginPage")
    public String login(){
        return "login";
    }

    // 用户注册的接口
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FROMED})
    @ResponseBody
    public CommonReturnType register(
            @RequestParam(name = "telephone") String telephone,
            @RequestParam(name = "otpCode") String otpCode,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "gender") Byte gender,
            @RequestParam(name = "age") Integer age,
            @RequestParam(name = "password") String password
    ) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 验证手机号和对应的otp验证码相符合
        // 之前把otp存放在了httpServletRequest中的Session中
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telephone);
        if (!StringUtils.equals(inSessionOtpCode, otpCode)){
            throw new BusinessException(EnBusinessError.PARAMETER_VALIDATION_ERROR, "验证码错误");
        }

        // 用户注册流程
        UserModel userModel = new UserModel();

        // 要对用户负责，因此得对用户密码进行加密操作，这里采用MD5方式
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        userModel.setAge(age);
        userModel.setGender(gender);
        userModel.setName(name);
        userModel.setTelphone(telephone);
        userModel.setRegisterMode("byphone");

        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    // 用户登陆的接口
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FROMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telephone") String telephone, @RequestParam(name = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 入参校验
        if (org.apache.commons.lang3.StringUtils.isEmpty(telephone)||
                org.apache.commons.lang3.StringUtils.isEmpty(password)){
            throw new BusinessException(EnBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        // 用户登录服务，用来校验用户登陆是否合法
        UserModel userModel = userService.validataLogin(telephone, this.EncodeByMd5(password));

        // 将登陆凭证加入到用户登录成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        return CommonReturnType.create(null);
    }

    public String EncodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newstr = base64Encoder.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }
}
