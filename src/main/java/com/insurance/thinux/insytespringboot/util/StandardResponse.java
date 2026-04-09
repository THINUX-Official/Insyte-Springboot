package com.insurance.thinux.insytespringboot.util;

import lombok.Data;

/**
 * @author: THINUX
 * @created: 21-Feb-26 - 04:45 PM
 */

@Data
public class StandardResponse<T> {
    private int code;
    private String message;
    private T data;

    public StandardResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
