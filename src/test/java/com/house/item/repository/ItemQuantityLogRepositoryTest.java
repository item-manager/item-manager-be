package com.house.item.repository;

import com.house.item.entity.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

@SpringBootTest
@Transactional
class ItemQuantityLogRepositoryTest {

    @Autowired
    ItemQuantityLogRepository quantityLogRepository;
    @Autowired
    EntityManager em;

    @Test
    void save() {
        //given
        User user = createUser();
        Location location = createLocation();
        Item item = createItem(user, location);

        ItemQuantityLog itemQuantityLog = getItemQuantityLog(item, QuantityType.PURCHASE, "mall", LocalDateTime.now(), 1000, 1);

        //when
        quantityLogRepository.save(itemQuantityLog);

        //then
        Long itemQuantityLogNo = itemQuantityLog.getItemQuantityLogNo();
        ItemQuantityLog findQuantityLog = em.find(ItemQuantityLog.class, itemQuantityLogNo);
        Assertions.assertThat(findQuantityLog).isSameAs(itemQuantityLog);
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

    Location createLocation() {
        Location room = Location.builder()
                .type(LocationType.ROOM)
                .name("room1")
                .build();
        em.persist(room);

        Location place = Location.builder()
                .type(LocationType.PLACE)
                .room(room)
                .name("desk")
                .build();
        em.persist(place);
        return place;
    }

    Item createItem(User user, Location location) {
        Item item = Item.builder()
                .user(user)
                .name("item1")
                .type(ItemType.CONSUMABLE)
                .location(location)
                .locationMemo("location memo")
                .quantity(1)
                .priority(1)
                .build();
        em.persist(item);
        return item;
    }

    ItemQuantityLog getItemQuantityLog(Item item, QuantityType type, String mall, LocalDateTime date, int price, int count) {
        return ItemQuantityLog.builder()
                .item(item)
                .type(type)
                .mall(mall)
                .date(date)
                .price(price)
                .count(count)
                .build();
    }
}