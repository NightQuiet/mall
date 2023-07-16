package com.hbwxz.enums;

/**
 * 授权类型
 * @author Night
 * @date 2023/7/16 13:12
 */
public enum AuthGrantType {
    implicit,

    client_credentials,
    authorization_code,

    refresh_token,

    password
}
