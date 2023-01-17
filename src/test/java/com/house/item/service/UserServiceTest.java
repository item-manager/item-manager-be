package com.house.item.service;

import com.house.item.domain.CreateUserRQ;
import com.house.item.entity.User;
import com.house.item.exception.NonUniqueUserIdException;
import com.house.item.repository.UserRepository;
import com.house.item.util.EncryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    void 정상_회원가입() throws Exception {

        //given
        CreateUserRQ createUserRQ = new CreateUserRQ("testUser", "testUser2@");

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
        CreateUserRQ createUserRQ1 = new CreateUserRQ("testUser", "testUser2@");
        userService.signUp(createUserRQ1);

        CreateUserRQ createUserRQ2 = new CreateUserRQ("testUser", "testUser3@");

        //when
        assertThatThrownBy(() -> userService.signUp(createUserRQ2))
                .isInstanceOf(NonUniqueUserIdException.class);
        //then
    }

}