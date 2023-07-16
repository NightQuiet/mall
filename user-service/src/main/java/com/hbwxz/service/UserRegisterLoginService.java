package com.hbwxz.service;

import com.hbwxz.pojo.OauthUser;
import com.hbwxz.response.CommonResponse;

/**
 * @author Night
 * @date 2023/7/16 14:09
 */
public interface UserRegisterLoginService {

    /**
     * 用户名密码
     * @param user
     * @return
     */
    public CommonResponse namePasswordRegister(OauthUser user);
}
