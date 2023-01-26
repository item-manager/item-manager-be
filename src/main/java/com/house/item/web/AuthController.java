package com.house.item.web;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.*;
import com.house.item.exception.IncorrectUserIdPasswordException;
import com.house.item.service.AuthService;
import com.house.item.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.INCORRECT_USER_ID_PASSWORD)
            )
    )
    @Operation(summary = "로그인")
    @PostMapping("/login")
    public Result<LoginUserRS> login(@RequestBody LoginUserRQ loginUserRQ) throws IncorrectUserIdPasswordException {
        SessionUser user = authService.login(loginUserRQ);
        SessionUtils.setAttribute(SessionConst.LOGIN_USER, user);

        LoginUserRS loginUserRS = LoginUserRS.builder()
                .userNo(user.getUserNo())
                .username(user.getUsername())
                .build();
        return Result.<LoginUserRS>builder()
                .data(loginUserRS)
                .build();
    }

    @Operation(summary = "로그아웃")
    @GetMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.<Void>builder()
                .code(200)
                .message("ok")
                .build();
    }
}
