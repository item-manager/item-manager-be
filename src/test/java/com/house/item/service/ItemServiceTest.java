package com.house.item.service;

import com.house.item.common.Props;
import com.house.item.domain.*;
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
import org.springframework.test.util.ReflectionTestUtils;
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
        Location room = createRoom(user, "room");
        Location location = createPlace(user, room, "place");

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

        FileUtil.deleteFile(props.getDir().getFile(), findItem.getPhotoName());
    }

    @Test
    void 물품_pk로_조회() throws Exception {
        //given
        User user = createSessionUser();
        Location room = createRoom(user, "room");
        Location location = createPlace(user, room, "place");
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
        Location room = createRoom(user, "room");
        Location location = createPlace(user, room, "place");
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
    void place_pk로_item_목록조회() throws Exception {
        //given
        User user = createSessionUser();
        Location room = createRoom(user, "room");
        Location place1 = createPlace(user, room, "place1");
        Location place2 = createPlace(user, room, "place2");
        Item item1 = getItem(user, place1, ItemType.CONSUMABLE, "item1", 2, 1);
        Item item2 = getItem(user, place1, ItemType.CONSUMABLE, "item2", 2, 1);
        Item item3 = getItem(user, place2, ItemType.CONSUMABLE, "item3", 2, 1);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        //when
        List<Item> items = itemService.getItemsInLocation(place1.getLocationNo());

        //then
        Assertions.assertThat(items)
                .containsExactly(item1, item2)
                .doesNotContain(item3);
    }

    @Test
    void room_pk로_item_목록조회() throws Exception {
        //given
        User user = createSessionUser();
        Location room1 = createRoom(user, "room1");
        Location room2 = createRoom(user, "room2");
        Location place1 = createPlace(user, room1, "place1");
        Location place2 = createPlace(user, room2, "place2");
        Item item1 = getItem(user, place1, ItemType.CONSUMABLE, "item1", 2, 1);
        Item item2 = getItem(user, place1, ItemType.CONSUMABLE, "item2", 2, 1);
        Item item3 = getItem(user, place2, ItemType.CONSUMABLE, "item3", 2, 1);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        //when
        List<Item> items = itemService.getItemsInLocation(room1.getLocationNo());

        //then
        Assertions.assertThat(items)
                .containsExactly(item1, item2)
                .doesNotContain(item3);
    }

    @Test
    void 소모품_검색() throws Exception {
        //given
        User user = createSessionUser();
        Location room = createRoom(user, "room");
        Location location = createPlace(user, room, "place");
        Label label1 = createLabel(user, "label1");
        Label label2 = createLabel(user, "label2");

        Item item1 = getItem(user, location, ItemType.CONSUMABLE, "item1", 1, 3);
        Item item2 = getItem(user, location, ItemType.CONSUMABLE, "item2", 2, 2);
        Item item3 = getItem(user, location, ItemType.CONSUMABLE, "item3", 3, 1);
        item1.getItemLabels().add(ItemLabel.builder()
                .item(item1)
                .label(Label.builder()
                        .labelNo(label1.getLabelNo())
                        .build())
                .build());
        item1.getItemLabels().add(ItemLabel.builder()
                .item(item1)
                .label(Label.builder()
                        .labelNo(label2.getLabelNo())
                        .build())
                .build());
        item3.getItemLabels().add(ItemLabel.builder()
                .item(item3)
                .label(Label.builder()
                        .labelNo(label1.getLabelNo())
                        .build())
                .build());
        item3.getItemLabels().add(ItemLabel.builder()
                .item(item3)
                .label(Label.builder()
                        .labelNo(label2.getLabelNo())
                        .build())
                .build());
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        em.flush();
        em.clear();

//        ConsumableItemsRQ consumableItemsRQ = new ConsumableItemsRQ("item", List.of(label1.getLabelNo(), label2.getLabelNo()), ConsumableItemsOrderByType.PRIORITY, "-", 1, 2);
        ConsumableItemsRQ consumableItemsRQ = new ConsumableItemsRQ("item", List.of(label1.getLabelNo(), label2.getLabelNo()), null, null, null, 2);
        ConsumableSearch consumableSearch = itemService.getConsumableSearch(consumableItemsRQ);

        //when
        List<ConsumableItemDTO> consumableItems = itemService.getConsumableItems(consumableSearch);

        //then
        Assertions.assertThat(consumableItems).hasSize(2);
    }

    @Test
    void 소모품검색조건_생성() throws Exception {
        //given
        User user = createSessionUser();

        ConsumableItemsRQ consumableItemsRQ = new ConsumableItemsRQ("name", List.of(1L, 2L), ConsumableItemsOrderByType.LATEST_CONSUME_DATE, "-", null, 5);

        //when
        ConsumableSearch search = (ConsumableSearch) ReflectionTestUtils.invokeMethod(itemService, "getConsumableSearch", consumableItemsRQ);

        //then
        Assertions.assertThat(search.getUserNo()).isEqualTo(user.getUserNo());
        Assertions.assertThat(search.getName()).isEqualTo("name");
        Assertions.assertThat(search.getLabelNos()).hasSize(2);
        Assertions.assertThat(search.getOrderBy()).isEqualTo("latestConsume");
        Assertions.assertThat(search.getSort()).isEqualTo("DESC");
        Assertions.assertThat(search.getPage()).isEqualTo(1);
        Assertions.assertThat(search.getSize()).isEqualTo(5);
    }

    @Test
    void 물품정보수정() throws Exception {
        //given
        User user = createSessionUser();
        Location room = createRoom(user, "room");
        Location location = createPlace(user, room, "place");
        Location location2 = createPlace(user, room, "place2");
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

    Location createRoom(User user, String name) {
        Location room = Location.builder()
                .user(user)
                .type(LocationType.ROOM)
                .name(name)
                .build();
        locationRepository.save(room);

        return room;
    }

    Location createPlace(User user, Location room, String name) {
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