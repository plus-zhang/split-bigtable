package io.banjuer.web.entity;

import io.banjuer.config.em.ResponseResult;

import java.io.Serializable;

public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 6769298460027574069L;

    /**
     * 类型
     */
    private Type type;

    /**
     * 信息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    public BaseResponse() {
    }

    public enum Type {

        /**
         *
         */
        success, warn, error;

        Type() {
        }
    }

    public BaseResponse(Type type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public BaseResponse(Type type, T data) {
        this.type = type;
        this.data = data;
    }

    public BaseResponse(Type type, T data, Object... args) {
        this.type = type;
        this.data = data;
    }

    public BaseResponse(Type type, String msg, T data) {
        this.type = type;
        this.msg = msg;
        this.data = data;
    }

    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(Type.success, ResponseResult.success.getDisplayName());
    }

    public static <T> BaseResponse<T> success(String msg) {
        return new BaseResponse<>(Type.success, msg);
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(Type.success, data);
    }

    public static <T> BaseResponse<T> success(String msg, T data) {
        return new BaseResponse<>(Type.success, msg, data);
    }

    public static <T> BaseResponse<T> error() {
        return new BaseResponse<>(Type.error, ResponseResult.error.getDisplayName());
    }

    public static <T> BaseResponse<T> error(String msg) {
        return new BaseResponse<>(Type.error, msg);
    }

    public static <T> BaseResponse<T> error(String msg, T data) {
        return new BaseResponse<>(Type.error, msg, data);
    }

    public static <T> BaseResponse<T> warn(String msg) {
        return new BaseResponse<>(Type.warn, msg);
    }

    public static <T> BaseResponse<T> warn(String msg, T data) {
        return new BaseResponse<>(Type.warn, msg, data);
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean isSuccess() {
        return Type.success.equals(this.type);
    }

    @Override
    public String toString() {
        return "BaseResponse [type=" + type + ", msg=" + msg + ", data=" + data + "]";
    }


}
