package cn.issboy.mengine.core.exception;

import cn.issboy.mengine.core.util.StringUtil;

/**
 * created by just on 18-5-3
 */
public class MException extends RuntimeException {

    public MException(String message){
        super(message);
    }

    public MException(Throwable cause){
        super(StringUtil.toString(cause));
    }
}
