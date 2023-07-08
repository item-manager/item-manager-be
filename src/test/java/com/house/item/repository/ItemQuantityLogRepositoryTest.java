package com.house.item.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumDto;
import com.house.item.domain.QuantityLogSumSearch;
import com.house.item.entity.Item;
import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.ItemType;
import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.QuantityType;
import com.house.item.entity.User;

@SpringBootTest
@Transactional
class ItemQuantityLogRepositoryTest {

    @Autowired
    ItemQuantityLogRepository quantityLogRepository;
    @Autowired
    EntityManager em;

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

        Pageable pageable = PageRequest.of(0, 2, Sort.Direction.ASC, "count");
        QuantityLogSearch search = QuantityLogSearch.builder()
            .item(
                Item.builder()
                    .itemNo(item1.getItemNo())
                    .build()
            )
            .pageable(pageable)
            .build();

        //when
        Page<ItemQuantityLog> logs = quantityLogRepository.findByItemNoAndTypeAndYearAndMonth(search);

        //then
        Assertions.assertThat(logs).containsExactly(quantityLog7, quantityLog1);

        Assertions.assertThat(logs.getTotalElements()).isEqualTo(3);
    }

    @Test
    void sumByDate() throws Exception {
        //given
        User user = createUser();
        Location location = createLocation();

        Item item = createItem(user, location);

        ItemQuantityLog quantityLog1 = getItemQuantityLog(item, QuantityType.PURCHASE, "mall1",
            LocalDateTime.now().minusYears(1), 1000, 2);
        ItemQuantityLog quantityLog2 = getItemQuantityLog(item, QuantityType.PURCHASE, "mall1",
            LocalDateTime.now().minusDays(20), 2000, 3);
        ItemQuantityLog quantityLog3 = getItemQuantityLog(item, QuantityType.PURCHASE, "mall1",
            LocalDateTime.now().minusDays(12), 3000, 4);
        ItemQuantityLog quantityLog4 = getItemQuantityLog(item, QuantityType.PURCHASE, "mall1",
            LocalDateTime.now().minusYears(1), 4000, 5);
        ItemQuantityLog quantityLog5 = getItemQuantityLog(item, QuantityType.PURCHASE, "mall1",
            LocalDateTime.now().minusDays(22), 5000, 6);
        ItemQuantityLog quantityLog6 = getItemQuantityLog(item, QuantityType.PURCHASE, "mall1",
            LocalDateTime.now().minusDays(22), 6000, 7);

        ItemQuantityLog quantityLog7 = getItemQuantityLog(item, QuantityType.CONSUME, null,
            LocalDateTime.now().minusDays(10), null, 1);
        ItemQuantityLog quantityLog8 = getItemQuantityLog(item, QuantityType.CONSUME, null,
            LocalDateTime.now().minusYears(1), null, 2);
        ItemQuantityLog quantityLog9 = getItemQuantityLog(item, QuantityType.CONSUME, null,
            LocalDateTime.now().minusDays(20), null, 3);
        ItemQuantityLog quantityLog10 = getItemQuantityLog(item, QuantityType.CONSUME, null,
            LocalDateTime.now().minusDays(2), null, 4);

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

        QuantityLogSumSearch search = QuantityLogSumSearch.builder()
            .item(item)
            // .year(2022)
            // .type(QuantityType.CONSUME)
            .build();

        //when
        List<QuantityLogSumDto> quantityLogSumDtos = quantityLogRepository.sumByDate(search);

        //then
        Assertions.assertThat(quantityLogSumDtos).hasSize(4);
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