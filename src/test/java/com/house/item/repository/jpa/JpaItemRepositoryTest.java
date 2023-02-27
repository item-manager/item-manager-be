package com.house.item.repository.jpa;

import com.house.item.domain.ConsumableItemDTO;
import com.house.item.domain.ConsumableSearch;
import com.house.item.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
@Slf4j
class JpaItemRepositoryTest {

    @Autowired
    JpaItemRepository itemRepository;
    @Autowired
    EntityManager em;

    @Test
    void save() {
        //given
        User user = createUser("user1", "username1");
        Location room = createRoom("room");
        Location location = createPlace(room, "desk");
        Item item = getItem(user, location, "soup", ItemType.CONSUMABLE, 1, 1);

        //when
        itemRepository.save(item);

        //then
        Item findItem = em.find(Item.class, item.getItemNo());
        Assertions.assertThat(findItem).isSameAs(item);
    }

    @Test
    void findOne() {
        //given
        User user = createUser("user1", "username1");
        Location room = createRoom("room");
        Location location = createPlace(room, "desk");
        Item item = getItem(user, location, "soup", ItemType.CONSUMABLE, 1, 1);
        em.persist(item);

        //when
        Item findItem = itemRepository.findOne(item.getItemNo()).get();

        //then
        Assertions.assertThat(findItem).isSameAs(item);
    }

    @Test
    void findByItemNoAndUserNo() {
        //given
        User user = createUser("user1", "username1");
        Location room = createRoom("room");
        Location location = createPlace(room, "desk");
        Item item = getItem(user, location, "soup", ItemType.CONSUMABLE, 1, 1);
        em.persist(item);

        //when
        Item findItem = itemRepository.findByItemNoAndUserNo(item.getItemNo(), user.getUserNo()).get();

        //then
        Assertions.assertThat(findItem).isSameAs(item);
    }

    @Test
    void findByPlaceNo() throws Exception {
        //given
        User user = createUser("user1", "username1");
        Location room = createRoom("room");
        Location location1 = createPlace(room, "desk");
        Location location2 = createPlace(room, "desk");
        Item item1 = getItem(user, location1, "item1", ItemType.CONSUMABLE, 1, 1);
        Item item2 = getItem(user, location1, "item2", ItemType.EQUIPMENT, 1, 1);
        Item item3 = getItem(user, location2, "item3", ItemType.EQUIPMENT, 1, 1);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        Long locationNo = location1.getLocationNo();
        Long locationNo2 = location2.getLocationNo();

        //when
        List<Item> items = itemRepository.findByPlaceNo(locationNo);

        //then
        Assertions.assertThat(items)
                .containsExactly(item1, item2)
                .doesNotContain(item3);
    }

    @Test
    void findByRoomNo() throws Exception {
        //given
        User user = createUser("user1", "username1");
        Location room1 = createRoom("room1");
        Location room2 = createRoom("room2");
        Location location1 = createPlace(room1, "desk");
        Location location2 = createPlace(room2, "desk");
        Item item1 = getItem(user, location1, "item1", ItemType.CONSUMABLE, 1, 1);
        Item item2 = getItem(user, location1, "item2", ItemType.EQUIPMENT, 1, 1);
        Item item3 = getItem(user, location2, "item3", ItemType.EQUIPMENT, 1, 1);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        Long roomNo1 = room1.getLocationNo();
        Long roomNo2 = room2.getLocationNo();

        //when
        List<Item> items = itemRepository.findByRoomNo(roomNo1);

        //then
        Assertions.assertThat(items)
                .containsExactly(item1, item2)
                .doesNotContain(item3);
    }

    @Test
    void findConsumableByNameAndLabel() throws Exception {
        //given
        User user = createUser("user1", "username1");
        Location room = createRoom("room");
        Location location = createPlace(room, "desk");
        Label label1 = createLabel(user, "label1");
        Label label2 = createLabel(user, "label2");
        Label label3 = createLabel(user, "label3");

        Item item1 = getItem(user, location, "item1", ItemType.CONSUMABLE, 1, 2);
        Item item2 = getItem(user, location, "item2", ItemType.CONSUMABLE, 2, 3);
        Item item3 = getItem(user, location, "item3", ItemType.CONSUMABLE, 3, 1);

        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        ItemLabel itemLabel1 = createItemLabel(item1, label1);
        ItemLabel itemLabel2 = createItemLabel(item1, label2);
        ItemLabel itemLabel3 = createItemLabel(item2, label2);
        ItemLabel itemLabel4 = createItemLabel(item2, label3);
        ItemLabel itemLabel5 = createItemLabel(item3, label3);

        createQuantityLog(item1, QuantityType.PURCHASE, LocalDateTime.now().minusDays(25));
        createQuantityLog(item1, QuantityType.PURCHASE, LocalDateTime.now().minusDays(20));
        createQuantityLog(item2, QuantityType.PURCHASE, LocalDateTime.now().minusDays(12));
        ItemQuantityLog quantityLog4 = createQuantityLog(item2, QuantityType.PURCHASE, LocalDateTime.now().minusDays(10));
        createQuantityLog(item3, QuantityType.PURCHASE, LocalDateTime.now().minusDays(22));

        createQuantityLog(item1, QuantityType.CONSUME, LocalDateTime.now().minusDays(10));
        ItemQuantityLog quantityLog7 = createQuantityLog(item2, QuantityType.CONSUME, LocalDateTime.now().minusDays(5));
        createQuantityLog(item3, QuantityType.CONSUME, LocalDateTime.now().minusDays(20));
        createQuantityLog(item3, QuantityType.CONSUME, LocalDateTime.now().minusDays(2));

        ConsumableSearch search = ConsumableSearch.builder()
                .userNo(user.getUserNo())
//                .name("2")
                .labelNos(List.of(label2.getLabelNo(), label3.getLabelNo()))
                .orderBy("priority")
                .sort("ASC")
                .size(2)
                .page(1)
                .build();

        //when
        List<ConsumableItemDTO> consumables = itemRepository.findConsumableByNameAndLabel(search);

        //then
        Assertions.assertThat(consumables).hasSize(1);
        Assertions.assertThat(consumables.get(0).getItem()).isEqualTo(item2);
        Assertions.assertThat(consumables.get(0).getLatestPurchase()).isEqualTo(quantityLog4.getDate());
        Assertions.assertThat(consumables.get(0).getLatestConsume()).isEqualTo(quantityLog7.getDate());
    }

    @Test
    void getConsumableTotalPage() throws Exception {
        //given
        User user = createUser("user1", "username1");
        Location room = createRoom("room");
        Location location = createPlace(room, "desk");
        Label label1 = createLabel(user, "label1");
        Label label2 = createLabel(user, "label2");
        Label label3 = createLabel(user, "label3");

        Item item1 = getItem(user, location, "item1", ItemType.CONSUMABLE, 1, 2);
        Item item2 = getItem(user, location, "item2", ItemType.CONSUMABLE, 2, 3);
        Item item3 = getItem(user, location, "item3", ItemType.CONSUMABLE, 3, 1);

        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        ItemLabel itemLabel1 = createItemLabel(item1, label1);
        ItemLabel itemLabel2 = createItemLabel(item1, label2);
        ItemLabel itemLabel3 = createItemLabel(item2, label2);
        ItemLabel itemLabel4 = createItemLabel(item2, label3);
        ItemLabel itemLabel5 = createItemLabel(item3, label3);

        createQuantityLog(item1, QuantityType.PURCHASE, LocalDateTime.now().minusDays(25));
        createQuantityLog(item1, QuantityType.PURCHASE, LocalDateTime.now().minusDays(20));
        createQuantityLog(item2, QuantityType.PURCHASE, LocalDateTime.now().minusDays(12));
        ItemQuantityLog quantityLog4 = createQuantityLog(item2, QuantityType.PURCHASE, LocalDateTime.now().minusDays(10));
        createQuantityLog(item3, QuantityType.PURCHASE, LocalDateTime.now().minusDays(22));

        createQuantityLog(item1, QuantityType.CONSUME, LocalDateTime.now().minusDays(10));
        ItemQuantityLog quantityLog7 = createQuantityLog(item2, QuantityType.CONSUME, LocalDateTime.now().minusDays(5));
        createQuantityLog(item3, QuantityType.CONSUME, LocalDateTime.now().minusDays(20));
        createQuantityLog(item3, QuantityType.CONSUME, LocalDateTime.now().minusDays(2));

        ConsumableSearch search = ConsumableSearch.builder()
                .userNo(user.getUserNo())
//                .name("2")
                .labelNos(List.of(label2.getLabelNo()))
//                .orderBy("priority")
//                .sort("ASC")
//                .size(2)
//                .page(1)
                .build();

        //when
        int rowCount = itemRepository.getConsumableRowCount(search);

        //then
        Assertions.assertThat(rowCount).isEqualTo(2);
    }

    User createUser(String id, String username) {
        User user = User.builder()
                .id(id)
                .password("user1pw")
                .salt("salt")
                .username(username)
                .build();
        em.persist(user);
        return user;
    }

    Location createRoom(String name) {
        Location room = Location.builder()
                .type(LocationType.ROOM)
                .name(name)
                .build();
        em.persist(room);
        return room;
    }

    Location createPlace(Location room, String name) {
        Location place = Location.builder()
                .type(LocationType.PLACE)
                .room(room)
                .name(name)
                .build();
        em.persist(place);
        return place;
    }

    Label createLabel(User user, String name) {
        Label label = Label.builder()
                .user(user)
                .name(name)
                .build();
        em.persist(label);
        return label;
    }

    Item getItem(User user, Location location, String name, ItemType type, int quantity, int priority) {
        Item item = Item.builder()
                .user(user)
                .name(name)
                .type(type)
                .location(location)
                .locationMemo("under the desk")
                .quantity(quantity)
                .priority(priority)
                .build();
        return item;
    }

    ItemLabel createItemLabel(Item item, Label label) {
        ItemLabel itemLabel = ItemLabel.builder()
                .item(item)
                .label(label)
                .build();
        em.persist(itemLabel);
        return itemLabel;
    }

    ItemQuantityLog createQuantityLog(Item item, QuantityType type, LocalDateTime date) {
        ItemQuantityLog quantityLog = ItemQuantityLog.builder()
                .item(item)
                .type(type)
                .date(date)
                .build();
        em.persist(quantityLog);
        return quantityLog;
    }
}