package com.house.item.service;

import com.house.item.domain.CreateLabel;
import com.house.item.domain.SessionUser;
import com.house.item.domain.UpdateLabelRQ;
import com.house.item.entity.*;
import com.house.item.exception.NonExistentLabelException;
import com.house.item.exception.NonUniqueLabelNameException;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

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

    @Test
    void 존재하지_않는_라벨조회() throws Exception {
        //given
        User user = createSessionUser();

        //when
        Assertions.assertThatThrownBy(() -> labelService.getLabel(0L))
                .isInstanceOf(NonExistentLabelException.class);
    }

    @Test
    void 다른_user_라벨조회() throws Exception {
        //given
        User user = User.builder()
                .id("id1")
                .password("pw")
                .salt("salt")
                .username("name1")
                .build();
        em.persist(user);
        Label label = getLabel(user, "label");
        em.persist(label);
        Long labelNo = label.getLabelNo();

        User anotherUser = createSessionUser();

        //when
        Assertions.assertThatThrownBy(() -> labelService.getLabel(labelNo))
                .isInstanceOf(NonExistentLabelException.class);
    }

    @Test
    void sessionUser_라벨목록_조회() throws Exception {
        //given
        User user = createSessionUser();
        Label label1 = getLabel(user, "label1");
        em.persist(label1);
        Label label2 = getLabel(user, "label2");
        em.persist(label2);

        //when
        List<Label> labels = labelService.getLabels();

        //then
        Assertions.assertThat(labels).containsExactly(label1, label2);
    }

    @Test
    void 라벨제거() throws Exception {
        //given
        User user = createSessionUser();
        Label label = getLabel(user, "label");
        em.persist(label);
        Long labelNo = label.getLabelNo();

        //when
        labelService.deleteLabel(labelNo);

        //then
        Label findLabel = em.find(Label.class, labelNo);
        Assertions.assertThat(findLabel).isNull();
    }

    @Test
    void 라벨정보수정() throws Exception {
        //given
        User user = createSessionUser();
        Label label = getLabel(user, "label");
        em.persist(label);
        Long labelNo = label.getLabelNo();

        UpdateLabelRQ updateLabelRQ = new UpdateLabelRQ();
        ReflectionTestUtils.setField(updateLabelRQ, "name", "new name");

        //when
        labelService.updateLabel(labelNo, updateLabelRQ);
        em.flush();
        em.clear();

        //then
        Label findLabel = em.find(Label.class, labelNo);
        Assertions.assertThat(findLabel.getName()).isEqualTo("new name");
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

    Location createLocation(User user) {
        Location room = Location.builder()
                .user(user)
                .name("room")
                .build();
        em.persist(room);

        Location place = Location.builder()
                .user(user)
                .name("place")
                .room(room)
                .build();
        em.persist(place);

        return place;
    }

    Item createItem(User user) {
        Item item = Item.builder()
                .user(user)
                .name("item")
                .type(ItemType.CONSUMABLE)
                .location(createLocation(user))
                .build();
        em.persist(item);

        return item;
    }

    Label getLabel(User user, String name) {
        return Label.builder()
                .user(user)
                .name(name)
                .build();
    }
}