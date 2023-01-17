package com.house.item.web;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.*;
import com.house.item.exception.NonExistentUserException;
import com.house.item.exception.NonUniqueUserIdException;
import com.house.item.service.UserService;
import com.house.item.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @ApiResponses({
            @ApiResponse(
                    responseCode = "400",
                    description = ExceptionCodeMessage.SwaggerDescription.NON_UNIQUE_USER_ID,
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
    @Operation(summary = "회원가입")
    @PostMapping
    public Result<CreateUserRS> createUser(@RequestBody CreateUserRQ createUserRQ) throws NonUniqueUserIdException {
        Long userNo = userService.signUp(createUserRQ);
        CreateUserRS createUserRS = CreateUserRS.builder()
                .userNo(userNo)
                .build();

        return Result.<CreateUserRS>builder()
                .data(createUserRS)
                .build();
    }

    @Operation(summary = "로그인한 유저 pk, id")
    @GetMapping("/session")
    public Result<SessionUser> getUser() {
        SessionUser loginUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        return Result.<SessionUser>builder()
                .data(loginUser)
                .build();
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "400",
                    description = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_USER,
                    content = @Content(schema = @Schema(implementation = ErrorResult.class))
            )
    })
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
