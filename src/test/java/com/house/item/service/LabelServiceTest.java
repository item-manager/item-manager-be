package com.house.item.service;

import com.house.item.domain.CreateLabel;
import com.house.item.domain.SessionUser;
import com.house.item.entity.Label;
import com.house.item.entity.User;
import com.house.item.exception.NonUniqueLabelNameException;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
@Slf4j
class LabelServiceTest {

    @Autowired
    private LabelService labelService;
    @Autowired
    private EntityManager em;

    @Test
    void 라벨생성() throws Exception {
        //given
        User user = createSessionUser();
        CreateLabel createLabel = CreateLabel.builder()
                .name("label")
                .build();

        //when
        Long labelNo = labelService.createLabel(createLabel);

        //then
        Label findLabel = em.find(Label.class, labelNo);
        Assertions.assertThat(findLabel.getUser()).isSameAs(user);
        Assertions.assertThat(findLabel.getName()).isSameAs(createLabel.getName());
    }

    @Test
    void 이미_존재하는_라벨이름으로_생성() throws Exception {
        //given
        User user = createSessionUser();
        Label label = getLabel(user, "label");
        em.persist(label);
        CreateLabel createLabel = CreateLabel.builder()
                .name("label")
                .build();

        //when
        Assertions.assertThatThrownBy(() -> labelService.createLabel(createLabel))
                .isInstanceOf(NonUniqueLabelNameException.class);
    }

    @Test
    void 라벨_pk로_조회() throws Exception {
        //given
        User user = createSessionUser();
        Label label = getLabel(user, "label");
        em.persist(label);

        //when
        Label findLabel = labelService.getLabel(label.getLabelNo());

        //then
        Assertions.assertThat(findLabel).isSameAs(label);
    }

    User createSessionUser() {
        User user = User.builder()
                .id("id")
                .password("pw")
                .salt("salt")
                .username("name")
                .build();
        em.persist(user);

        SessionUser sessionUser = SessionUser.builder()
                .userNo(user.getUserNo())
                .username(user.getUsername())
                .build();

        SessionUtils.setAttribute(SessionConst.LOGIN_USER, sessionUser);
        return user;
    }

    Label getLabel(User user, String name) {
        return Label.builder()
                .user(user)
                .name(name)
                .build();
    }
}