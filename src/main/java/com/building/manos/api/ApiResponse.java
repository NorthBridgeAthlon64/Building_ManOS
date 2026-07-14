package com.building.manos.api;

/**
 * 统一 API 响应体 {@code {code, message, data}}。
 *
 * @param <T> data 类型
 * @author 马玉
 * @since 2026-07-14
 */
public class ApiResponse<T> {

    public static final int CODE_OK = 0;
    public static final int CODE_BAD_REQUEST = 40001;
    public static final int CODE_NOT_FOUND = 40401;
    public static final int CODE_SERVER_ERROR = 50000;

    private final int code;
    private final String message;
    private final T data;

    /**
     * @param code    业务码，0 表示成功
     * @param message 提示信息
     * @param data    载荷，可为 null
     */
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应。
     *
     * @param data 载荷
     * @param <T>  载荷类型
     * @return 响应
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(CODE_OK, "ok", data);
    }

    /**
     * 成功响应（无载荷）。
     *
     * @return 响应
     */
    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(CODE_OK, "ok", null);
    }

    /**
     * 失败响应。
     *
     * @param code    错误码
     * @param message 错误信息
     * @param <T>     载荷类型
     * @return 响应
     */
    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
