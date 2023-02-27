package com.house.item.exception;

public class UnableToDeleteLocationInUseException extends RuntimeException {
    public UnableToDeleteLocationInUseException() {
    }

    public UnableToDeleteLocationInUseException(String message) {
        super(message);
    }

    public UnableToDeleteLocationInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToDeleteLocationInUseException(Throwable cause) {
        super(cause);
    }

    public UnableToDeleteLocationInUseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
