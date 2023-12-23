package com.xuecheng.base.exception;

import java.io.Serializable;

/**
 * @author iooi
 * description TODO
 * @data 2023/12/22 21:36
 */
public class RestErrorResponse implements Serializable {

    private String errMessage;

    public RestErrorResponse(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
