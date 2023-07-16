package com.hbwxz.response;

/**
 * 响应码枚举类
 * @author Night
 * @date 2023/7/15 13:05
 */
public enum ResponseCode {

    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证");

    private final Integer code;
    private final String message;

    private ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
