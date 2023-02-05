package com.house.item.repository;

import com.house.item.entity.Label;
import com.house.item.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

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
        Label label = getLabel(user, "label");

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
        Label label = getLabel(user, "label");
        em.persist(label);

        //when
        Label findLabel = labelRepository.findOne(label.getLabelNo()).get();

        //then
        Assertions.assertThat(findLabel).isSameAs(label);
    }

    @Test
    void findByLabelNoAndUserNo() {
        //given
        User user = createUser();
        Label label = getLabel(user, "label");
        em.persist(label);

        //when
        Label findLabel = labelRepository.findByLabelNoAndUserNo(label.getLabelNo(), user.getUserNo()).get();

        //then
        Assertions.assertThat(findLabel).isSameAs(label);
    }

    @Test
    void findByNameAndUserNo() throws Exception {
        //given
        User user = createUser();
        Label label = getLabel(user, "label");
        em.persist(label);

        //when
        Label findLabel = labelRepository.findByNameAndUserNo(label.getName(), user.getUserNo()).get();

        //then
        Assertions.assertThat(findLabel).isSameAs(label);
    }

    @Test
    void findByUserNo() throws Exception {
        //given
        User user = createUser();
        Label label1 = getLabel(user, "label1");
        em.persist(label1);
        Label label2 = getLabel(user, "label2");
        em.persist(label2);

        //when
        List<Label> labels = labelRepository.findByUserNo(user.getUserNo());

        //then
        Assertions.assertThat(labels).containsExactly(label1, label2);
    }

    @Test
    void deleteByLabelNo() throws Exception {
        //given
        User user = createUser();
        Label label = getLabel(user, "label");
        em.persist(label);
        Long labelNo = label.getLabelNo();

        //when
        labelRepository.deleteByLabelNo(labelNo);

        //then
        Label findLabel = em.find(Label.class, labelNo);
        Assertions.assertThat(findLabel).isNull();
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

    Label getLabel(User user, String name) {
        return Label.builder()
                .user(user)
                .name(name)
                .build();
    }
}