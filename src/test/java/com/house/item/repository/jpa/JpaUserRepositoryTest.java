package com.house.item.repository.jpa;

import com.house.item.entity.User;
import com.house.item.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
@Slf4j
class JpaUserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void save() throws Exception {
        //given
        User user = User.builder()
                .id("testUser")
                .password("testUser")
                .salt("salt")
                .username("username")
                .build();

        //when
        userRepository.save(user);

        //then
        User findUser = userRepository.findOne(user.getUserNo()).get();
        Assertions.assertThat(findUser).isSameAs(user);
    }

    @Test
    void findOne() throws Exception {
        //given
        User user = User.builder()
                .id("testUser")
                .password("testUser")
                .salt("salt")
                .username("username")
                .build();
        userRepository.save(user);

        //when
        User findUser = userRepository.findOne(user.getUserNo()).get();

        //then
        Assertions.assertThat(findUser).isSameAs(user);
    }

    @Test
    void findById() throws Exception {
        //given
        User user = User.builder()
                .id("testUser")
                .password("testUser")
                .salt("salt")
                .username("username")
                .build();
        userRepository.save(user);

        //when
        Optional<User> optionalUser = userRepository.findById("testUser");

        //then
        Assertions.assertThat(optionalUser.get().getUserNo()).isEqualTo(user.getUserNo());
    }

    @Test
    void delete() throws Exception {
        //given
        User user = User.builder()
                .id("testUser")
                .password("testUser")
                .salt("salt")
                .username("username")
                .build();
        userRepository.save(user);

        //when
        userRepository.delete(user);

        //then
        Optional<User> optionalUser = userRepository.findOne(user.getUserNo());
        Assertions.assertThat(optionalUser).isEmpty();
    }
}