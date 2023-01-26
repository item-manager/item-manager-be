package com.house.item.exception;

public class NotLocationTypeRoomException extends RuntimeException {
    public NotLocationTypeRoomException() {
    }

    public NotLocationTypeRoomException(String message) {
        super(message);
    }

    public NotLocationTypeRoomException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotLocationTypeRoomException(Throwable cause) {
        super(cause);
    }

    public NotLocationTypeRoomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
