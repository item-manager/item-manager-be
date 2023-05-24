package com.house.item.web;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.*;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentUserException;
import com.house.item.exception.NonUniqueUserIdException;
import com.house.item.service.AuthService;
import com.house.item.service.UserService;
import com.house.item.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

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
        User loginUser = authService.getLoginUser();

        UserRS userRS = userService.userToUserRS(loginUser);

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
        User loginUser = authService.getLoginUser();

        userService.updateUserInfo(loginUser, updateUserInfoRQ);

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
        User loginUser = authService.getLoginUser();

        userService.changePassword(loginUser, changePasswordRQ);

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
        SessionUser loginUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        userService.removeUser(loginUser.getUserNo());
        session.invalidate();

        return Result.<Void>builder()
                .code(200)
                .message("ok")
                .build();
    }
}
