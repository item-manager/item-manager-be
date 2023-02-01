package com.house.item.web.advice;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.ErrorResult;
import com.house.item.exception.NonExistentItemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ItemControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult nonExistentItemException(NonExistentItemException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.NON_EXISTENT_ITEM.code())
                .message(e.getMessage())
                .build();
    }
}
