package com.house.item.web.advice;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.ErrorResult;
import com.house.item.exception.*;
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
                .code(ExceptionCodeMessage.NON_EXISTENT_USER.code())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult nonUniqueUserIdException(NonUniqueUserIdException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.NON_UNIQUE_USER_ID.code())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult nonUniqueUsernameException(NonUniqueUsernameException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.NON_UNIQUE_USERNAME.code())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult incorrectUserIdPassword(IncorrectUserIdPasswordException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.INCORRECT_USER_ID_PASSWORD.code())
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler
    public ErrorResult nonExistentSessionUserException(NonExistentSessionUserException e) {
        return ErrorResult.builder()
                .code(ExceptionCodeMessage.NON_EXISTENT_SESSION_USER.code())
                .message(e.getMessage())
                .build();
    }
}
