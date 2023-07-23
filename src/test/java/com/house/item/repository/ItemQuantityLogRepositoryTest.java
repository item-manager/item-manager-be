package com.house.item.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.domain.QuantityLogDTO;
import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumDTO;
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

    @DisplayName("pk, User로 ItemQuantityLog 조회")
    @Test
    void findByIdAndUser() throws Exception {
        //given
        User user = createUser("user1");
        em.persist(user);

        Location location = createLocation(user, "location1");
        em.persist(location);

        Item item = createItem(location, "item1");
        em.persist(item);

        ItemQuantityLog quantityLog = getItemQuantityLog(item, null, "mall1", null, null, 1);
        em.persist(quantityLog);

        //when
        Optional<ItemQuantityLog> findLog = quantityLogRepository.findByIdAndUser(
            quantityLog.getItemQuantityLogNo(), user);

        //then
        assertThat(findLog).isNotEmpty();
        assertThat(findLog.get()).extracting("mall", "item.name")
            .containsExactly("mall1", "item1");
    }

    @Test
    void findByItemNoAndTypeAndYearAndMonth() throws Exception {
        //given
        User user = createUser("user1");
        em.persist(user);
        Location location = createLocation(user, "location1");
        em.persist(location);

        Item item1 = createItem(location, "item1");
        Item item2 = createItem(location, "item2");
        Item item3 = createItem(location, "item3");
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        ItemQuantityLog quantityLog1 = getItemQuantityLog(item1, QuantityType.PURCHASE, "mall1",
            LocalDateTime.now().minusDays(25), 1000, 2);
        ItemQuantityLog quantityLog2 = getItemQuantityLog(item1, QuantityType.PURCHASE, "mall1",
            LocalDateTime.now().minusDays(20), 2000, 3);
        ItemQuantityLog quantityLog3 = getItemQuantityLog(item2, QuantityType.PURCHASE, "mall1",
            LocalDateTime.now().minusDays(12), 3000, 4);
        ItemQuantityLog quantityLog4 = getItemQuantityLog(item2, QuantityType.PURCHASE, "mall1",
            LocalDateTime.now().minusDays(10), 4000, 5);
        ItemQuantityLog quantityLog5 = getItemQuantityLog(item3, QuantityType.PURCHASE, "mall1",
            LocalDateTime.now().minusDays(22), 5000, 6);
        ItemQuantityLog quantityLog6 = getItemQuantityLog(item3, QuantityType.PURCHASE, "mall1",
            LocalDateTime.now().minusDays(22), 6000, 7);

        ItemQuantityLog quantityLog7 = getItemQuantityLog(item1, QuantityType.CONSUME, null,
            LocalDateTime.now().minusDays(10), null, 1);
        ItemQuantityLog quantityLog8 = getItemQuantityLog(item2, QuantityType.CONSUME, null,
            LocalDateTime.now().minusDays(5), null, 2);
        ItemQuantityLog quantityLog9 = getItemQuantityLog(item3, QuantityType.CONSUME, null,
            LocalDateTime.now().minusDays(20), null, 3);
        ItemQuantityLog quantityLog10 = getItemQuantityLog(item3, QuantityType.CONSUME, null,
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
        Page<QuantityLogDTO> logs = quantityLogRepository.findByItemNoAndTypeAndYearAndMonth(search);

        //then
        assertThat(logs).extracting("quantityLog")
            .containsExactly(quantityLog7, quantityLog1);

        assertThat(logs.getTotalElements()).isEqualTo(3);
    }

    @Test
    void sumByDate() throws Exception {
        //given
        User user = createUser("user1");
        em.persist(user);
        Location location = createLocation(user, "location1");
        em.persist(location);

        Item item = createItem(location, "item1");
        em.persist(item);

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
        List<QuantityLogSumDTO> quantityLogSumDTOS = quantityLogRepository.sumByDate(search);

        //then
        assertThat(quantityLogSumDTOS).hasSize(4);
    }

    @DisplayName("User의 ItemQuantityLog의 mall 중복제거 목록 조회")
    @Test
    void findDistinctMalls() throws Exception {
        //given
        User user1 = createUser("user1");
        em.persist(user1);

        Location location1 = createLocation(user1, "location1");
        em.persist(location1);

        Item item1 = createItem(location1, "item1");
        em.persist(item1);

        ItemQuantityLog log1 = getItemQuantityLog(item1, null, "mall1", null, null, 1);
        ItemQuantityLog log2 = getItemQuantityLog(item1, null, "mall2", null, null, 1);
        ItemQuantityLog log3 = getItemQuantityLog(item1, null, "mall2", null, null, 1);
        ItemQuantityLog log4 = getItemQuantityLog(item1, null, "mall3", null, null, 1);
        em.persist(log1);
        em.persist(log2);
        em.persist(log3);
        em.persist(log4);

        //when
        List<String> malls = quantityLogRepository.findDistinctMalls(user1);

        //then
        assertThat(malls).hasSize(3)
            .containsExactlyInAnyOrder("mall1", "mall2", "mall3");
    }

    User createUser(String id) {
        User user = User.builder()
            .id(id)
            .password("user1pw")
            .salt("salt")
            .username("username1")
            .build();
        return user;
    }

    Location createLocation(User user, String name) {
        Location place = Location.builder()
            .user(user)
            .type(LocationType.PLACE)
            .name(name)
            .build();
        return place;
    }

    Item createItem(Location location, String name) {
        Item item = Item.builder()
            .name(name)
            .type(ItemType.CONSUMABLE)
            .location(location)
            .quantity(1)
            .priority(1)
            .build();
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