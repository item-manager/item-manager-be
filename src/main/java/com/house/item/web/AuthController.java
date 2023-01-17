package com.house.item.web;

import com.house.item.domain.LoginUserRQ;
import com.house.item.domain.LoginUserRS;
import com.house.item.domain.Result;
import com.house.item.domain.SessionUser;
import com.house.item.service.AuthService;
import com.house.item.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public Result<LoginUserRS> login(@RequestBody LoginUserRQ loginUserRQ) {
        SessionUser user = authService.login(loginUserRQ);
        SessionUtils.setAttribute(SessionConst.LOGIN_USER, user);

        LoginUserRS loginUserRS = LoginUserRS.builder()
                .userNo(user.getUserNo())
                .id(user.getId())
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
