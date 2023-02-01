package com.house.item.web.advice;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.ErrorResult;
import com.house.item.exception.NonExistentPlaceException;
import com.house.item.exception.NonExistentRoomException;
import com.house.item.exception.NotLocationTypeRoomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class LocationControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult nonExistentRoomException(NonExistentRoomException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.NON_EXISTENT_ROOM.code())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult nonExistentPlaceException(NonExistentPlaceException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.NON_EXISTENT_PLACE.code())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult notLocationTypeRoomException(NotLocationTypeRoomException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.NOT_LOCATION_TYPE_ROOM.code())
                .message(e.getMessage())
                .build();
    }
}
