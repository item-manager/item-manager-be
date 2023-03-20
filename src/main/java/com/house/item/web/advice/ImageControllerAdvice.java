package com.house.item.web.advice;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.ErrorResult;
import com.house.item.exception.NotContentTypeImageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ImageControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult notContentTypeImageException(NotContentTypeImageException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.NOT_CONTENT_TYPE_IMAGE_EXCEPTION.code())
                .message(e.getMessage())
                .build();
    }
}
