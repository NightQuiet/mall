package com.hbwxz.response;

/**
 * @author Night
 * @date 2023/7/15 13:25
 */
public class ResponseUtils {

    /**
     * 成功响应
     * @return
     */
    public static CommonResponse success(Object data) {
        return CommonResponse.builder()
                .code(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    /**
     * 失败响应
     * @return
     */
    public static CommonResponse fail(Integer code, String errorMessage, Object data) {
        return CommonResponse.builder()
                .code(code)
                .message(errorMessage)
                .data(data)
                .build();
    }
}
