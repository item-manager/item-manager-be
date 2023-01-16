package com.house.item.exception;

public class IncorrectUserIdPasswordException extends RuntimeException {
    public IncorrectUserIdPasswordException() {
        super();
    }

    public IncorrectUserIdPasswordException(String message) {
        super(message);
    }

    public IncorrectUserIdPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectUserIdPasswordException(Throwable cause) {
        super(cause);
    }

    protected IncorrectUserIdPasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
