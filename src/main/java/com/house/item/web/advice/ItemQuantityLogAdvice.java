package com.house.item.web.advice;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.ErrorResult;
import com.house.item.exception.NonExistentItemQuantityLogException;
import com.house.item.exception.SubtractCountExceedItemQuantityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ItemQuantityLogAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult nonExistentItemQuantityLogException(NonExistentItemQuantityLogException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.NON_EXISTENT_ITEM_QUANTITY_LOG.code())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult subtractCountExceedItemQuantityException(SubtractCountExceedItemQuantityException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.SUBTRACT_COUNT_EXCEEDED_ITEM_QUANTITY_EXCEPTION.code())
                .message(e.getMessage())
                .build();
    }
}
