package com.qingyi.helper.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {

    private int code;
    private String message;
    private T data;
    private long timestamp;

    private R() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> R<T> ok() {
        return restResult(null, 200, "success");
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, 200, "success");
    }

    public static <T> R<T> ok(T data, String message) {
        return restResult(data, 200, message);
    }

    public static <T> R<T> failed() {
        return restResult(null, 500, "服务异常");
    }

    public static <T> R<T> failed(String message) {
        return restResult(null, 500, message);
    }

    public static <T> R<T> failed(int code, String message) {
        return restResult(null, code, message);
    }

    private static <T> R<T> restResult(T data, int code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setData(data);
        return r;
    }
}
