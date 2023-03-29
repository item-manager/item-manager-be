package com.house.item.repository;

import com.house.item.domain.QuantityLogSearch;
import com.house.item.entity.*;
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

    @Test
    void findByItemNoAndTypeAndYearAndMonth() throws Exception {
        //given
        User user = createUser();
        Location location = createLocation();

        Item item1 = createItem(user, location);
        Item item2 = createItem(user, location);
        Item item3 = createItem(user, location);

        ItemQuantityLog quantityLog1 = getItemQuantityLog(item1, QuantityType.PURCHASE, "mall1", LocalDateTime.now().minusDays(25), 1000, 2);
        ItemQuantityLog quantityLog2 = getItemQuantityLog(item1, QuantityType.PURCHASE, "mall1", LocalDateTime.now().minusDays(20), 2000, 3);
        ItemQuantityLog quantityLog3 = getItemQuantityLog(item2, QuantityType.PURCHASE, "mall1", LocalDateTime.now().minusDays(12), 3000, 4);
        ItemQuantityLog quantityLog4 = getItemQuantityLog(item2, QuantityType.PURCHASE, "mall1", LocalDateTime.now().minusDays(10), 4000, 5);
        ItemQuantityLog quantityLog5 = getItemQuantityLog(item3, QuantityType.PURCHASE, "mall1", LocalDateTime.now().minusDays(22), 5000, 6);
        ItemQuantityLog quantityLog6 = getItemQuantityLog(item3, QuantityType.PURCHASE, "mall1", LocalDateTime.now().minusDays(22), 6000, 7);

        ItemQuantityLog quantityLog7 = getItemQuantityLog(item1, QuantityType.CONSUME, null, LocalDateTime.now().minusDays(10), null, 1);
        ItemQuantityLog quantityLog8 = getItemQuantityLog(item2, QuantityType.CONSUME, null, LocalDateTime.now().minusDays(5), null, 2);
        ItemQuantityLog quantityLog9 = getItemQuantityLog(item3, QuantityType.CONSUME, null, LocalDateTime.now().minusDays(20), null, 3);
        ItemQuantityLog quantityLog10 = getItemQuantityLog(item3, QuantityType.CONSUME, null, LocalDateTime.now().minusDays(2), null, 4);

        em.persist(quantityLog1);
        em.persist(quantityLog2);
        em.persist(quantityLog3);
        em.persist(quantityLog4);
        em.persist(quantityLog5);
        em.persist(quantityLog6);
        em.persist(quantityLog7);
        em.persist(quantityLog8);
        em.persist(quantityLog9);
        em.persist(quantityLog10);

        QuantityLogSearch search = QuantityLogSearch.builder()
                .item(
                        Item.builder()
                                .itemNo(item1.getItemNo())
                                .build()
                )
                .orderBy("l.count")
                .sort("asc")
                .page(1)
                .size(2)
                .build();

        //when
        List<ItemQuantityLog> logs = quantityLogRepository.findByItemNoAndTypeAndYearAndMonth(search);

        //then
        Assertions.assertThat(logs).containsExactly(quantityLog7, quantityLog1);
    }

    @Test
    void getLogsByItemNoRowCount() throws Exception {
        //given
        User user = createUser();
        Location location = createLocation();

        Item item1 = createItem(user, location);
        Item item2 = createItem(user, location);
        Item item3 = createItem(user, location);

        ItemQuantityLog quantityLog1 = getItemQuantityLog(item1, QuantityType.PURCHASE, "mall1", LocalDateTime.now().minusDays(25), 1000, 2);
        ItemQuantityLog quantityLog2 = getItemQuantityLog(item1, QuantityType.PURCHASE, "mall1", LocalDateTime.now().minusDays(20), 2000, 3);
        ItemQuantityLog quantityLog3 = getItemQuantityLog(item2, QuantityType.PURCHASE, "mall1", LocalDateTime.now().minusDays(12), 3000, 4);
        ItemQuantityLog quantityLog4 = getItemQuantityLog(item2, QuantityType.PURCHASE, "mall1", LocalDateTime.now().minusDays(10), 4000, 5);
        ItemQuantityLog quantityLog5 = getItemQuantityLog(item3, QuantityType.PURCHASE, "mall1", LocalDateTime.now().minusDays(22), 5000, 6);
        ItemQuantityLog quantityLog6 = getItemQuantityLog(item3, QuantityType.PURCHASE, "mall1", LocalDateTime.now().minusDays(22), 6000, 7);

        ItemQuantityLog quantityLog7 = getItemQuantityLog(item1, QuantityType.CONSUME, null, LocalDateTime.now().minusDays(10), null, 1);
        ItemQuantityLog quantityLog8 = getItemQuantityLog(item2, QuantityType.CONSUME, null, LocalDateTime.now().minusDays(5), null, 2);
        ItemQuantityLog quantityLog9 = getItemQuantityLog(item3, QuantityType.CONSUME, null, LocalDateTime.now().minusDays(20), null, 3);
        ItemQuantityLog quantityLog10 = getItemQuantityLog(item3, QuantityType.CONSUME, null, LocalDateTime.now().minusDays(2), null, 4);

        em.persist(quantityLog1);
        em.persist(quantityLog2);
        em.persist(quantityLog3);
        em.persist(quantityLog4);
        em.persist(quantityLog5);
        em.persist(quantityLog6);
        em.persist(quantityLog7);
        em.persist(quantityLog8);
        em.persist(quantityLog9);
        em.persist(quantityLog10);

        QuantityLogSearch search = QuantityLogSearch.builder()
                .item(
                        Item.builder()
                                .itemNo(item1.getItemNo())
                                .build()
                )
                .orderBy("l.count")
                .sort("asc")
                .page(1)
                .size(2)
                .build();

        //when
        long rowCount = quantityLogRepository.getLogsByItemNoRowCount(search);

        //then
        Assertions.assertThat(rowCount).isEqualTo(3);
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

    ItemQuantityLog getItemQuantityLog(Item item, QuantityType type, String mall, LocalDateTime date, Integer price, Integer count) {
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