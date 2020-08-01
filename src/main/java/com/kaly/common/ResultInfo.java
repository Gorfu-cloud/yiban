package com.kaly.common;

import lombok.Data;
import lombok.ToString;

/**
 * Created by 国瑚 on 2020/6/29.
 */

//规范结果集
@Data
public class ResultInfo<T> {

    //状态码： ok:0 异常：400 后台问题：500
    private  Integer code;

    private String message;

    private T data;

    public ResultInfo() {
    }

    public ResultInfo(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResultInfo(Integer code, String message) {
        this(code,message,null);
    }

    public ResultInfo(T data) {
        this(200,"操作成功",data);
    }


}
