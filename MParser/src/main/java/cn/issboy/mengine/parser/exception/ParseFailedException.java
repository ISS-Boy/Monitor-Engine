package cn.issboy.mengine.parser.exception;

/**
 * created by just on 18-1-3
 */
public class ParseFailedException extends RuntimeException {

    public ParseFailedException(String message) {
        super(message);
    }

    public ParseFailedException(String message, Throwable t) {
        super(message, t);
    }


}
