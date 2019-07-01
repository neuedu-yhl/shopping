package com.neuedu.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 封装高复用的响应对象
 */


@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HigherResponse<T> {

    private HigherResponse()
    {

    }
    private HigherResponse(Integer status)
    {
        this.status = status;
    }
    private HigherResponse(Integer status,T t)
    {
        this.status = status;
        this.data = t;
    }

    private HigherResponse(Integer status,String msg)
    {
        this.status = status;
        this.msg = msg;
    }

    private HigherResponse(Integer status,String msg,T t)
    {
        this.status = status;
        this.msg = msg;
        this.data = t;
    }
    /**
     * success:
     *             1.  status data
     *             2.  status
     *             3.  status msg
     *             4.  status msg data
     *        failed:  `
     *             1. status msg`
     */

    private Integer status;

    private T data;

    private String msg;

    /**
     * 提供对外公开的方法
     */
    @JsonIgnore
    public boolean isResponseSuccess()
    {
        return this.status == StatusUtil.SUCCESSSTATUS;
    }
    //Success
    public static HigherResponse getResponseSuccess()
    {
        return new HigherResponse(StatusUtil.SUCCESSSTATUS);
    }
    public static HigherResponse getResponseSuccess(String msg)
    {
        return new HigherResponse(StatusUtil.SUCCESSSTATUS,msg);
    }
    public static <T> HigherResponse getResponseSuccess(T t)
    {
        return new HigherResponse(StatusUtil.SUCCESSSTATUS,t);
    }
    public static <T> HigherResponse getResponseSuccess(String msg,T t)
    {
        return new HigherResponse(StatusUtil.SUCCESSSTATUS,msg,t);
    }


    //Failed
    @JsonIgnore
    public boolean isResponseFailed()
    {
        return this.status == StatusUtil.FAILEDSTATUS;
    }
    //Failed方法
    public static HigherResponse getResponseFailed()
    {
        return new HigherResponse(StatusUtil.FAILEDSTATUS);
    }
    public static HigherResponse getResponseFailed(String msg) {
        return new HigherResponse(StatusUtil.FAILEDSTATUS, msg);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}