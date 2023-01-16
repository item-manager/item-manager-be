package com.house.item.service;

import com.house.item.domain.CreateUserRQ;
import com.house.item.domain.LoginUserRQ;
import com.house.item.domain.SessionUser;
import com.house.item.exception.IncorrectUserIdPasswordException;
import com.house.item.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    AuthService authService;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void init() {
        CreateUserRQ user1 = new CreateUserRQ("user1", "user1!");
        CreateUserRQ user2 = new CreateUserRQ("user2", "user2@");

        userService.signUp(user1);
        userService.signUp(user2);
    }

    @Test
    void 정상_로그인() throws Exception {
        //given
        LoginUserRQ loginUserRQ = new LoginUserRQ("user1", "user1!");

        //when
        SessionUser loginUser = authService.login(loginUserRQ);

        //then
        assertThat(loginUser.getId()).isEqualTo(loginUserRQ.getId());
    }

    @Test
    void 존재하지_않는_아이디로_로그인() throws Exception {
        //given
        LoginUserRQ loginUserRQ = new LoginUserRQ("user3", "user1!");

        //when
        assertThatThrownBy(() -> authService.login(loginUserRQ))
                .isInstanceOf(IncorrectUserIdPasswordException.class);

        //then
    }

    @Test
    void 아이디_패스워드_불일치() throws Exception {
        //given
        LoginUserRQ loginUserRQ = new LoginUserRQ("user2", "user1!");

        //when
        assertThatThrownBy(() -> authService.login(loginUserRQ))
                .isInstanceOf(IncorrectUserIdPasswordException.class);

        //then
    }

}