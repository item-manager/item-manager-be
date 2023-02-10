package com.house.item.service;

import com.house.item.common.Props;
import com.house.item.domain.CreateItemRQ;
import com.house.item.domain.SessionUser;
import com.house.item.domain.UpdateItemRQ;
import com.house.item.entity.*;
import com.house.item.repository.LocationRepository;
import com.house.item.repository.UserRepository;
import com.house.item.util.FileUtil;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@Slf4j
class ItemServiceTest {

    @Autowired
    ItemService itemService;
    @Autowired
    EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    Props props;

    @Test
    void 물품생성() throws Exception {
        //given
        User user = createSessionUser();
        Location location = createLocation(user, "place");

        MultipartFile photo = new MockMultipartFile(
                "fileName",
                "/Users/yurim/Downloads/KakaoTalk_Photo_2023-01-27-16-28-12-3.jpeg",
                "image/jpeg",
                new FileInputStream("/Users/yurim/Downloads/KakaoTalk_Photo_2023-01-27-16-28-12-3.jpeg"));

        Label label1 = createLabel(user, "label1");
        Long label1No = label1.getLabelNo();

        Label label2 = createLabel(user, "label2");
        Long label2No = label2.getLabelNo();

        CreateItemRQ createItemRQ = new CreateItemRQ(
                "item1",
                ItemType.CONSUMABLE,
                location.getLocationNo(),
                "location memo",
                photo,
                1,
                new ArrayList<>(List.of(label1No, label2No)));

        //when
        Long itemNo = itemService.createItem(createItemRQ);

        //then
        Item findItem = em.find(Item.class, itemNo);
        Assertions.assertThat(findItem.getQuantity()).isZero();

        FileUtil.deleteFile(props.getDir().getPhoto(), findItem.getPhotoName());
    }

    @Test
    void 물품_pk로_조회() throws Exception {
        //given
        User user = createSessionUser();
        Location location = createLocation(user, "place");
        Item item = getItem(user, location, ItemType.CONSUMABLE, "item1", 2, 1);
        em.persist(item);

        //when
        Item findItem = itemService.getItem(item.getItemNo());

        //then
        Assertions.assertThat(findItem).isSameAs(item);
    }

    @Test
    void user_pk로_item_목록조회() throws Exception {
        //given
        User user = createSessionUser();
        Location location = createLocation(user, "place");
        Item item1 = getItem(user, location, ItemType.CONSUMABLE, "item1", 2, 1);
        Item item2 = getItem(user, location, ItemType.CONSUMABLE, "item2", 2, 1);
        Item item3 = getItem(user, location, ItemType.CONSUMABLE, "item3", 2, 1);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        //when
        List<Item> items = itemService.getItems();

        //then
        Assertions.assertThat(items).hasSize(3)
                .containsExactly(item1, item2, item3);
    }

    @Test
    void 물품정보수정() throws Exception {
        //given
        User user = createSessionUser();
        Location location = createLocation(user, "place");
        Location location2 = createLocation(user, "place2");
        Label label1 = createLabel(user, "label1");
        Label label2 = createLabel(user, "label2");
        Item item1 = getItem(user, location, ItemType.CONSUMABLE, "item1", 2, 1);
        item1.getItemLabels().add(ItemLabel.builder()
                .item(item1)
                .label(Label.builder()
                        .labelNo(label1.getLabelNo())
                        .build())
                .build());
        em.persist(item1);
        Long itemNo = item1.getItemNo();

        em.flush();
        em.clear();

        UpdateItemRQ updateItemRQ = new UpdateItemRQ("new item", ItemType.EQUIPMENT, location2.getLocationNo(), "locationMemo", null, 3, List.of(label2.getLabelNo()));

        //when
        itemService.updateItem(itemNo, updateItemRQ);
        em.flush();
        em.clear();

        //then
        Item findItem = em.find(Item.class, itemNo);
        Assertions.assertThat(findItem.getName()).isEqualTo("new item");
        Assertions.assertThat(findItem.getType()).isEqualTo(ItemType.EQUIPMENT);
        Assertions.assertThat(findItem.getLocation().getName()).isEqualTo("place2");
        Assertions.assertThat(findItem.getLocationMemo()).isEqualTo("locationMemo");
        Assertions.assertThat(findItem.getPriority()).isEqualTo(3);
        for (ItemLabel itemLabel : findItem.getItemLabels()) {
            Assertions.assertThat(itemLabel.getLabel().getLabelNo()).isEqualTo(label2.getLabelNo());
        }
    }

    User createSessionUser() {
        User user = User.builder()
                .id("id")
                .password("pw")
                .salt("salt")
                .username("name")
                .build();
        userRepository.save(user);

        SessionUser sessionUser = SessionUser.builder()
                .userNo(user.getUserNo())
                .username(user.getUsername())
                .build();

//        SessionUser sessionUser = SessionUser.builder()
//                .userNo(1L)
//                .username(user.getUsername())
//                .build();
        SessionUtils.setAttribute(SessionConst.LOGIN_USER, sessionUser);
        return user;
    }

    Location createLocation(User user, String name) {
        Location room = Location.builder()
                .user(user)
                .type(LocationType.ROOM)
                .name("room")
                .build();
        locationRepository.save(room);

        Location place = Location.builder()
                .user(user)
                .type(LocationType.PLACE)
                .room(room)
                .name(name)
                .build();
        locationRepository.save(place);

        return place;
    }

    private Label createLabel(User user, String name) {
        Label label = Label.builder()
                .user(user)
                .name(name)
                .build();
        em.persist(label);
        return label;
    }

    Item getItem(User user, Location location, ItemType type, String name, int quantity, int priority) {
        return Item.builder()
                .user(user)
                .type(type)
                .name(name)
                .location(location)
                .locationMemo("location memo")
                .quantity(quantity)
                .priority(priority)
                .build();
    }

}

//    @Rollback(value = false)
//    @Test
//    void test() throws Exception {
//        //given
//        User user = createSessionUser();
//        Location room1 = Location.builder()
//                .user(user)
//                .type(LocationType.ROOM)
//                .name("거실")
//                .build();
//        em.persist(room1);
//
//        Location room2 = Location.builder()
//                .user(user)
//                .type(LocationType.ROOM)
//                .name("안방")
//                .build();
//        em.persist(room2);
//
//        Location place1 = Location.builder()
//                .user(user)
//                .type(LocationType.PLACE)
//                .room(room1)
//                .name("탁자 서랍")
//                .build();
//        em.persist(place1);
//
//        Location place2 = Location.builder()
//                .user(user)
//                .type(LocationType.PLACE)
//                .room(room2)
//                .name("옷장")
//                .build();
//        em.persist(place2);
//
//        Location place3 = Location.builder()
//                .user(user)
//                .type(LocationType.PLACE)
//                .room(room2)
//                .name("화장대")
//                .build();
//        em.persist(place3);
//
//        Item item1 = Item.builder()
//                .user(user)
//                .name("로션")
//                .type(ItemType.CONSUMABLE)
//                .location(place3)
//                .locationMemo("장 아래칸")
//                .photoName("70efec72-9953-4a5d-9b55-845cc75fa62b.jpeg")
//                .quantity(1)
//                .priority(1)
//                .build();
//        em.persist(item1);
//
//        Item item2 = Item.builder()
//                .user(user)
//                .name("제습제")
//                .type(ItemType.CONSUMABLE)
//                .location(place2)
//                .locationMemo("2번째장 아래 서랍")
//                .quantity(3)
//                .priority(1)
//                .build();
//        em.persist(item2);
//
//        Label label1 = Label.builder()
//                .user(user)
//                .name("label1")
//                .build();
//        em.persist(label1);
//
//        Label label2 = Label.builder()
//                .user(user)
//                .name("label2")
//                .build();
//        em.persist(label2);
//
//        Label label3 = Label.builder()
//                .user(user)
//                .name("label3")
//                .build();
//        em.persist(label3);
//
//        ItemLabel itemLabel1 = ItemLabel.builder()
//                .item(item1)
//                .label(label1)
//                .build();
//        em.persist(itemLabel1);
//
//        ItemLabel itemLabel2 = ItemLabel.builder()
//                .item(item1)
//                .label(label2)
//                .build();
//        em.persist(itemLabel2);
//
//        ItemLabel itemLabel3 = ItemLabel.builder()
//                .item(item2)
//                .label(label3)
//                .build();
//        em.persist(itemLabel3);
//
//        Long userNo = user.getUserNo();
//
//        em.flush();
//        em.clear();
//
//        String jpql = "select l from Label l";
//        List<Label> resultList = em.createQuery(jpql, Label.class)
//                .getResultList();
////        for (Label label : resultList) {
////            System.out.println("label.getItemLabels() = " + label.getItemLabels());
////        }
//
//        //when
//
//        //then
//    }