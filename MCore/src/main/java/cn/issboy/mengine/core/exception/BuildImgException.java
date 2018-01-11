package cn.issboy.mengine.core.exception;

/**
 * created by just on 18-1-11
 */
public class BuildImgException extends RuntimeException {


    public BuildImgException(String message){super(message);}

    public BuildImgException(String message, Throwable t){
        super(message,t);
    }
}
