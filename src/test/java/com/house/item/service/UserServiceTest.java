package com.house.item.service;

import com.house.item.domain.ChangePasswordRQ;
import com.house.item.domain.CreateUserRQ;
import com.house.item.entity.User;
import com.house.item.exception.NonUniqueUserIdException;
import com.house.item.exception.NonUniqueUsernameException;
import com.house.item.repository.UserRepository;
import com.house.item.util.EncryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    @Test
    void 정상_회원가입() throws Exception {

        //given
        CreateUserRQ createUserRQ = new CreateUserRQ("testUser", "testUser2@", "user");

        //when
        Long createdId = userService.signUp(createUserRQ);

        //then
        User findUser = userRepository.findOne(createdId).get();
        assertThat(findUser.getId()).isEqualTo(createUserRQ.getId());
        assertThat(findUser.getPassword())
                .isEqualTo(EncryptUtils.getEncrypt(createUserRQ.getPassword(), findUser.getSalt()));
    }

    @Test
    void 중복_ID_회원가입() throws Exception {
        //given
        CreateUserRQ createUserRQ1 = new CreateUserRQ("testUser", "testUser2@", "user1");
        userService.signUp(createUserRQ1);

        CreateUserRQ createUserRQ2 = new CreateUserRQ("testUser", "testUser3@", "user2");

        //when
        assertThatThrownBy(() -> userService.signUp(createUserRQ2))
                .isInstanceOf(NonUniqueUserIdException.class);
        //then
    }

    @Test
    void 중복_username_회원가입() throws Exception {
        //given
        CreateUserRQ createUserRQ1 = new CreateUserRQ("testUser1", "testUser2@", "user1");
        userService.signUp(createUserRQ1);

        CreateUserRQ createUserRQ2 = new CreateUserRQ("testUser2", "testUser3@", "user1");

        //when
        assertThatThrownBy(() -> userService.signUp(createUserRQ2))
                .isInstanceOf(NonUniqueUsernameException.class);
        //then
    }

    @Test
    void 비밀번호_변경() throws Exception {
        //given
        CreateUserRQ createUserRQ = new CreateUserRQ("testUser", "testUser2@", "user");
        Long createdId = userService.signUp(createUserRQ);

        User createUser = userRepository.findOne(createdId).get();

        ChangePasswordRQ changePasswordRQ = new ChangePasswordRQ();
        ReflectionTestUtils.setField(changePasswordRQ, "currentPassword", "testUser2@");
        ReflectionTestUtils.setField(changePasswordRQ, "newPassword", "newPassword2@");

        //when
        userService.changePassword(createUser, changePasswordRQ);

        em.flush();
        em.clear();

        //then
        User findUser = userRepository.findOne(createdId).get();
        Assertions.assertThat(findUser.getPassword()).isEqualTo(EncryptUtils.getEncrypt(changePasswordRQ.getNewPassword(), findUser.getSalt()));
    }
}