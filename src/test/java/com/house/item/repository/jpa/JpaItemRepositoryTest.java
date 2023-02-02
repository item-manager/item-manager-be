package com.house.item.repository.jpa;

import com.house.item.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

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
        User user = createUser();
        Location location = createLocation();
        Item item = Item.builder()
                .user(user)
                .name("soup")
                .type(ItemType.CONSUMABLE)
                .location(location)
                .locationMemo("under the desk")
                .quantity(1)
                .priority(1)
                .build();

        //when
        itemRepository.save(item);

        //then
        Item findItem = em.find(Item.class, item.getItemNo());
        Assertions.assertThat(findItem).isSameAs(item);
    }

    @Test
    void findOne() {
        //given
        User user = createUser();
        Location location = createLocation();
        Item item = Item.builder()
                .user(user)
                .name("soup")
                .type(ItemType.CONSUMABLE)
                .location(location)
                .locationMemo("under the desk")
                .quantity(1)
                .priority(1)
                .build();
        em.persist(item);

        //when
        Item findItem = itemRepository.findOne(item.getItemNo()).get();

        //then
        Assertions.assertThat(findItem).isSameAs(item);
    }

    @Test
    void findByItemNoAndUserNo() {
        //given
        User user = createUser();
        Location location = createLocation();
        Item item = Item.builder()
                .user(user)
                .name("soup")
                .type(ItemType.CONSUMABLE)
                .location(location)
                .locationMemo("under the desk")
                .quantity(1)
                .priority(1)
                .build();
        em.persist(item);

        //when
        Item findItem = itemRepository.findByItemNoAndUserNo(item.getItemNo(), user.getUserNo()).get();

        //then
        Assertions.assertThat(findItem).isSameAs(item);
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
}