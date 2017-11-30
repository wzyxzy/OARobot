package com.zgty.oarobot.bean;

/**
 * Created by zy on 2017/11/29.
 */

public class GetAccessBack {

    /**
     * code : 0
     * message : OK
     * result : reject
     */

    private int code;
    private String message;
    private String result;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
