package org.destiny.activiti.util;

import lombok.Data;

@Data
public class Result {

    private int code;
    private String message;
    private Object data;

    public static Result genSuccess(Object data) {
        Result result = new Result();
        result.code = 200;
        result.data = data;
        return result;
    }

    public static Result genFail(int code, String message) {
        Result result = new Result();
        result.code = code;
        result.message = message;
        return result;
    }
}
