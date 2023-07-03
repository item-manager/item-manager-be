package com.house.item.web;

import javax.servlet.http.HttpSession;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.ChangePasswordRQ;
import com.house.item.domain.CreateUserRQ;
import com.house.item.domain.CreateUserRS;
import com.house.item.domain.ErrorResult;
import com.house.item.domain.Result;
import com.house.item.domain.SessionUser;
import com.house.item.domain.UpdateUserInfoRQ;
import com.house.item.domain.UserRS;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentUserException;
import com.house.item.exception.NonUniqueUserIdException;
import com.house.item.service.UserService;
import com.house.item.util.SessionUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_UNIQUE_USER_ID),
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_UNIQUE_USERNAME)
                    }
            )
    )
    @Operation(summary = "회원가입")
    @PostMapping
    public Result<CreateUserRS> createUser(@Validated @RequestBody CreateUserRQ createUserRQ) throws NonUniqueUserIdException {
        Long userNo = userService.signUp(createUserRQ);
        CreateUserRS createUserRS = CreateUserRS.builder()
                .userNo(userNo)
                .build();

        return Result.<CreateUserRS>builder()
                .data(createUserRS)
                .build();
    }

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_USER)
                    }
            )
    )
    @Operation(summary = "로그인한 회원 정보")
    @GetMapping("/session")
    public Result<UserRS> getUser() {
        SessionUser sessionUser = SessionUtils.getSessionUser();
        User user = userService.getUser(sessionUser.getUserNo());

        UserRS userRS = userService.userToUserRS(user);

        return Result.<UserRS>builder()
            .data(userRS)
            .build();
    }

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_USER)
                    }
            )
    )
    @Operation(summary = "회원 정보 수정")
    @PatchMapping
    public Result<Void> updateUserInfo(@RequestBody UpdateUserInfoRQ updateUserInfoRQ) {
        SessionUser sessionUser = SessionUtils.getSessionUser();
        User user = userService.getUser(sessionUser.getUserNo());

        userService.updateUserInfo(user, updateUserInfoRQ);

        return Result.<Void>builder()
            .code(200)
            .message("ok")
            .build();
    }

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_USER),
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.INCORRECT_USER_ID_PASSWORD)
                    }
            )
    )
    @Operation(summary = "회원 비밀번호 수정")
    @PatchMapping("/newPassword")
    public Result<Void> changePassword(@Validated @RequestBody ChangePasswordRQ changePasswordRQ) {
        SessionUser sessionUser = SessionUtils.getSessionUser();
        User user = userService.getUser(sessionUser.getUserNo());

        userService.changePassword(user, changePasswordRQ);

        return Result.<Void>builder()
            .code(200)
            .message("ok")
            .build();
    }

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_USER)
            )
    )
    @Operation(summary = "로그인한 유저 탈퇴")
    @DeleteMapping
    public Result<Void> deleteUser(HttpSession session) throws NonExistentUserException {
        SessionUser sessionUser = SessionUtils.getSessionUser();
        userService.removeUser(sessionUser.getUserNo());
        session.invalidate();

        return Result.<Void>builder()
            .code(200)
            .message("ok")
            .build();
    }
}
