package com.pyg.core.entity;

import java.io.Serializable;

/**
 * 结果封装
 */
public class Result implements Serializable {
    private Boolean flag;
    private String message;

    public Result(Boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
