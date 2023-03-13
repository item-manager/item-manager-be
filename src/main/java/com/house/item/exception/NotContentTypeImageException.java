package com.house.item.exception;

public class NotContentTypeImageException extends RuntimeException {
    public NotContentTypeImageException() {
    }

    public NotContentTypeImageException(String message) {
        super(message);
    }

    public NotContentTypeImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotContentTypeImageException(Throwable cause) {
        super(cause);
    }

    public NotContentTypeImageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
