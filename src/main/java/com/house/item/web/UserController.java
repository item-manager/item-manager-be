package com.house.item.web;

import com.house.item.domain.CreateUserRQ;
import com.house.item.domain.CreateUserRS;
import com.house.item.domain.Result;
import com.house.item.domain.SessionUser;
import com.house.item.exception.NonUniqueUserIdException;
import com.house.item.service.UserService;
import com.house.item.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "로그인한 유저 탈퇴")
    @DeleteMapping
    public Result<Void> deleteUser(HttpSession session) {
        SessionUser loginUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        userService.removeUser(loginUser.getUserNo());
        session.invalidate();

        return Result.<Void>builder()
                .code(200)
                .message("ok")
                .build();
    }
}
