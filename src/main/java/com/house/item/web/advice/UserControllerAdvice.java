package com.house.item.web.advice;

import com.house.item.domain.ErrorResult;
import com.house.item.exception.IncorrectUserIdPasswordException;
import com.house.item.exception.NonExistentUserException;
import com.house.item.exception.NonUniqueUserIdException;
import com.house.item.web.AuthController;
import com.house.item.web.UserController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {UserController.class, AuthController.class})
@Slf4j
public class UserControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult nonExistentUserException(NonExistentUserException e) {
        return ErrorResult.builder()
                .code(1001)
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult nonUniqueUserIdException(NonUniqueUserIdException e) {
        return ErrorResult.builder()
                .code(1002)
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult incorrectUserIdPassword(IncorrectUserIdPasswordException e) {
        return ErrorResult.builder()
                .code(1003)
                .message(e.getMessage())
                .build();
    }
}
