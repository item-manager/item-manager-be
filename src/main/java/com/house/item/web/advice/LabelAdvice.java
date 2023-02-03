package com.house.item.web.advice;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.ErrorResult;
import com.house.item.exception.NonExistentLabelException;
import com.house.item.exception.NonUniqueLabelNameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class LabelAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult nonExistentLabelException(NonExistentLabelException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.NON_EXISTENT_LABEL.code())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult nonUniqueLabelNameException(NonUniqueLabelNameException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.NON_UNIQUE_LABEL_NAME.code())
                .message(e.getMessage())
                .build();
    }
}
