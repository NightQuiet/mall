package com.hbwxz.dto.reponse;

import com.hbwxz.pojo.OauthClientDetails;
import com.hbwxz.pojo.OauthUser;
import lombok.Data;

/**
 * 用户注册登录返回类
 * @author Night
 * @date 2023/7/16 14:57
 */
@Data
public class UserRegisterLoginResp {
    /**
     * oauthUser
     */
    private OauthUser oauthUser;

    /**
     * clientDetails
     */
    private OauthClientDetails clientDetails;
}
