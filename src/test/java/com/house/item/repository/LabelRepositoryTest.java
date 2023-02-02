package com.house.item.repository;

import com.house.item.entity.Label;
import com.house.item.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
class LabelRepositoryTest {

    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private EntityManager em;

    @Test
    void save() {
        //given
        User user = createUser();
        Label label = Label.builder()
                .user(user)
                .name("label")
                .build();

        //when
        labelRepository.save(label);

        //then
        Label findLabel = em.find(Label.class, label.getLabelNo());
        Assertions.assertThat(findLabel).isSameAs(label);
    }

    @Test
    void findOne() {
        //given
        User user = createUser();
        Label label = Label.builder()
                .user(user)
                .name("label")
                .build();
        em.persist(label);

        //when
        Label findLabel = labelRepository.findOne(label.getLabelNo()).get();

        //then
        Assertions.assertThat(findLabel).isSameAs(label);
    }

    User createUser() {
        User user = User.builder()
                .id("user1")
                .password("user1pw")
                .salt("salt")
                .username("username1")
                .build();
        em.persist(user);
        return user;
    }
}