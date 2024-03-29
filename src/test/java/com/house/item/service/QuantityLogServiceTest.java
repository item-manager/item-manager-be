package com.house.item.service;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.domain.ConsumeItemRQ;
import com.house.item.domain.PurchaseItemServiceRQ;
import com.house.item.entity.Item;
import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.ItemType;
import com.house.item.entity.Location;
import com.house.item.entity.QuantityType;
import com.house.item.entity.User;
import com.house.item.exception.SubtractCountExceedItemQuantityException;

@SpringBootTest
@Transactional
class QuantityLogServiceTest {
    @Autowired
    QuantityLogService quantityLogService;
    @Autowired
    EntityManager em;

    @Test
    void 물품_구매() throws Exception {
        //given
        User user = createUser("id");
        em.persist(user);

        Location location = createLocation(user);

        Item item = createItem(location, 0);
        Long itemNo = item.getItemNo();

        PurchaseItemServiceRQ request = PurchaseItemServiceRQ.builder()
            .user(user)
            .itemId(itemNo)
            .mall("mall1")
            .date(LocalDateTime.now())
            .price(1000)
            .count(10)
            .build();

        //when
        int resultQuantity = quantityLogService.purchaseItem(request);

        //then
        Item findItem = em.find(Item.class, itemNo);
        Assertions.assertThat(findItem.getQuantity()).isEqualTo(resultQuantity);
    }

    @Test
    void 물품_사용() throws Exception {
        //given
        User user = createUser("id");
        em.persist(user);

        Location location = createLocation(user);

        Item item = createItem(location, 10);
        Long itemNo = item.getItemNo();

        ConsumeItemRQ consumeItemRQ = new ConsumeItemRQ();
        ReflectionTestUtils.setField(consumeItemRQ, "date", LocalDateTime.now());
        ReflectionTestUtils.setField(consumeItemRQ, "count", 10);

        //when
        int resultQuantity = quantityLogService.consumeItem(itemNo, consumeItemRQ, user);

        //then
        Item findItem = em.find(Item.class, itemNo);
        Assertions.assertThat(findItem.getQuantity()).isEqualTo(resultQuantity);
    }

    @Test
    void 사용_수량이_물품_수량을_초과() throws Exception {
        //given
        User user = createUser("id");
        em.persist(user);

        Location location = createLocation(user);

        Item item = createItem(location, 1);
        Long itemNo = item.getItemNo();

        ConsumeItemRQ consumeItemRQ = new ConsumeItemRQ();
        ReflectionTestUtils.setField(consumeItemRQ, "date", LocalDateTime.now());
        ReflectionTestUtils.setField(consumeItemRQ, "count", 10);

        //when
        Assertions.assertThatThrownBy(() -> quantityLogService.consumeItem(itemNo, consumeItemRQ, user))
            .isInstanceOf(SubtractCountExceedItemQuantityException.class);

        //then
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

    Item createItem(Location location, int quantity) {
        Item item = Item.builder()
            .name("item")
            .type(ItemType.CONSUMABLE)
            .location(location)
            .quantity(quantity)
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