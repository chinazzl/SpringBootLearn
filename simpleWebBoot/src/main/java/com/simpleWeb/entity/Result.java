package com.simpleWeb.entity;

/**
 * @Author: zhang zhao lin
 * @Description:
 * @Date:Create：in 2020/12/29 20:52
 * @Modified By：
 */
public class Result {
    private boolean success;

    private Object result;


    public Result(boolean success, Object result) {
        this.success = success;
        this.result = result;
    }

    public static Result ok(boolean success, Object result) {

        return new Result(success, result);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }


}
