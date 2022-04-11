package org.drools.demo.common;

/**********************************
 * @author zhang zhao lin
 * @date 2022年04月11日 22:12
 * @Description:
 **********************************/
public class Result {

    private int code;

    private String message;

    private Object data;

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public Result(Object data) {
        this.data = data;
    }

    public static Result ok(Object data) {
        return new Result(200, data);
    }

    public static Result error(int code, String message) {
        return new Result(code, message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
