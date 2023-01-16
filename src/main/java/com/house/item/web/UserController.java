package com.house.item.web;

import com.house.item.domain.CreateUserRQ;
import com.house.item.domain.CreateUserRS;
import com.house.item.domain.Result;
import com.house.item.domain.SessionUser;
import com.house.item.exception.NonUniqueUserIdException;
import com.house.item.service.UserService;
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

    @GetMapping("/session")
    public Result<SessionUser> getUser(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) SessionUser loginUser) {
        return Result.<SessionUser>builder()
                .data(loginUser)
                .build();
    }

    @DeleteMapping
    public Result<Void> deleteUser(HttpSession session,
                                   @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) SessionUser loginUser) {

        userService.removeUser(loginUser.getUserNo());
        session.invalidate();

        return Result.<Void>builder()
                .code(200)
                .message("ok")
                .build();
    }
}
