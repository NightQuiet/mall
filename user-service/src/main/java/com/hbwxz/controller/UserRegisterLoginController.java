package com.hbwxz.controller;


import com.hbwxz.pojo.OauthUser;
import com.hbwxz.response.CommonResponse;
import com.hbwxz.service.UserRegisterLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/register")
public class UserRegisterLoginController {

    @Autowired
    private UserRegisterLoginService userRegisterLoginService;

    //用户名 + 密码
    @PostMapping("/name-password")
    public CommonResponse namePasswordRegister(@RequestBody OauthUser user) {
        return userRegisterLoginService.namePasswordRegister(user);
    }

    //手机 + 验证码
    // @PostMapping("/phone-code")
    // public CommonResponse phoneCodeRegister(@RequestParam String phoneNumber,
    //                                         @RequestParam String code) {
    //     return userService.phoneCodeRegister(phoneNumber, code);
    // }
    //
    // //gitee 第三方账号登录
    // //这个接口是 第三方平台调用咱们的，这个叫回调接口
    // @RequestMapping("/gitee")
    // public CommonResponse thirdPartGiteeCallback(HttpServletRequest request) {
    //     return userService.thirdPartGiteeCallback(request);
    // }
    //
    // @RequestMapping("/login")
    // public CommonResponse login(@RequestParam String username,
    //                             @RequestParam String password) {
    //     return userService.login(username, password);
    // }
}
