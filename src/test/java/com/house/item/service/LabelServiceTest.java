package com.house.item.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.domain.CreateLabel;
import com.house.item.domain.UpdateLabelRQ;
import com.house.item.entity.Item;
import com.house.item.entity.ItemType;
import com.house.item.entity.Label;
import com.house.item.entity.Location;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentLabelException;
import com.house.item.exception.NonUniqueLabelNameException;

import lombok.extern.slf4j.Slf4j;

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
        User user = createUser("user1");
        em.persist(user);
        CreateLabel createLabel = CreateLabel.builder()
            .name("label")
            .build();

        //when
        Long labelNo = labelService.createLabel(createLabel, user);

        //then
        Label findLabel = em.find(Label.class, labelNo);
        Assertions.assertThat(findLabel.getUser()).isSameAs(user);
        Assertions.assertThat(findLabel.getName()).isSameAs(createLabel.getName());
    }

    @Test
    void 이미_존재하는_라벨이름으로_생성() throws Exception {
        //given
        User user = createUser("user1");
        em.persist(user);
        Label label = getLabel(user, "label");
        em.persist(label);
        CreateLabel createLabel = CreateLabel.builder()
            .name("label")
            .build();

        //when
        Assertions.assertThatThrownBy(() -> labelService.createLabel(createLabel, user))
            .isInstanceOf(NonUniqueLabelNameException.class);
    }

    @Test
    void 라벨_pk로_조회() throws Exception {
        //given
        User user = createUser("user1");
        em.persist(user);
        Label label = getLabel(user, "label");
        em.persist(label);

        //when
        Label findLabel = labelService.getLabel(label.getLabelNo(), user);

        //then
        Assertions.assertThat(findLabel).isSameAs(label);
    }

    @Test
    void 존재하지_않는_라벨조회() throws Exception {
        //given
        User user = createUser("user1");
        em.persist(user);

        //when
        Assertions.assertThatThrownBy(() -> labelService.getLabel(0L, user))
            .isInstanceOf(NonExistentLabelException.class);
    }

    @Test
    void sessionUser_라벨목록_조회() throws Exception {
        //given
        User user = createUser("user1");
        Label label1 = getLabel(user, "label1");
        em.persist(label1);
        Label label2 = getLabel(user, "label2");
        em.persist(label2);

        //when
        List<Label> labels = labelService.getLabels(user);

        //then
        Assertions.assertThat(labels).containsExactly(label1, label2);
    }

    @Test
    void 라벨제거() throws Exception {
        //given
        User user = createUser("user1");
        Label label = getLabel(user, "label");
        em.persist(label);
        Long labelNo = label.getLabelNo();

        //when
        labelService.deleteLabel(labelNo, user);

        //then
        Label findLabel = em.find(Label.class, labelNo);
        Assertions.assertThat(findLabel).isNull();
    }

    @Test
    void 라벨정보수정() throws Exception {
        //given
        User user = createUser("user1");
        Label label = getLabel(user, "label");
        em.persist(label);
        Long labelNo = label.getLabelNo();

        UpdateLabelRQ updateLabelRQ = new UpdateLabelRQ();
        ReflectionTestUtils.setField(updateLabelRQ, "name", "new name");

        //when
        labelService.updateLabel(labelNo, updateLabelRQ, user);
        em.flush();
        em.clear();

        //then
        Label findLabel = em.find(Label.class, labelNo);
        Assertions.assertThat(findLabel.getName()).isEqualTo("new name");
    }

    User createUser(String id) {
        return User.builder()
            .id(id)
            .password("pw")
            .salt("salt")
            .username("name")
            .build();
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