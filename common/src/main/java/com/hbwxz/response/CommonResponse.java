package com.hbwxz.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author Night
 * @date 2023/7/15 13:04
 */
@Data
@Builder
public class CommonResponse {

    private Integer code;

    private String message;

    private Object data;
}
