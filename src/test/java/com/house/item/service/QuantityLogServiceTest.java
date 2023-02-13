package com.house.item.service;

import com.house.item.domain.ConsumeItemRQ;
import com.house.item.domain.PurchaseItemRQ;
import com.house.item.domain.SessionUser;
import com.house.item.entity.*;
import com.house.item.exception.SubtractCountExceedItemQuantityException;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

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
        User user = createSessionUser();
        Item item = createItem(user, 0);
        Long itemNo = item.getItemNo();

        PurchaseItemRQ purchaseItemRQ = PurchaseItemRQ.builder()
                .mall("mall")
                .date(LocalDateTime.now())
                .unitPrice(1000)
                .count(10)
                .build();

        //when
        int resultQuantity = quantityLogService.purchaseItem(itemNo, purchaseItemRQ);

        //then
        Item findItem = em.find(Item.class, itemNo);
        Assertions.assertThat(findItem.getQuantity()).isEqualTo(resultQuantity);
    }

    @Test
    void 물품_사용() throws Exception {
        //given
        User user = createSessionUser();
        Item item = createItem(user, 10);
        Long itemNo = item.getItemNo();

        ConsumeItemRQ consumeItemRQ = ConsumeItemRQ.builder()
                .date(LocalDateTime.now())
                .count(10)
                .build();

        //when
        int resultQuantity = quantityLogService.consumeItem(itemNo, consumeItemRQ);

        //then
        Item findItem = em.find(Item.class, itemNo);
        Assertions.assertThat(findItem.getQuantity()).isEqualTo(resultQuantity);
    }

    @Test
    void 사용_수량이_물품_수량을_초과() throws Exception {
        //given
        User user = createSessionUser();
        Item item = createItem(user, 1);
        Long itemNo = item.getItemNo();

        ConsumeItemRQ consumeItemRQ = ConsumeItemRQ.builder()
                .date(LocalDateTime.now())
                .count(2)
                .build();

        //when
        Assertions.assertThatThrownBy(() -> quantityLogService.consumeItem(itemNo, consumeItemRQ))
                .isInstanceOf(SubtractCountExceedItemQuantityException.class);

        //then
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

    Item createItem(User user, int quantity) {
        Item item = Item.builder()
                .user(user)
                .name("item")
                .type(ItemType.CONSUMABLE)
                .location(createLocation(user))
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