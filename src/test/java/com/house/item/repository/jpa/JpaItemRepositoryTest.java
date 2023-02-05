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
        User user = createUser("user1", "username1");
        Location location = createLocation();
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
        Location location = createLocation();
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
        Location location = createLocation();
        Item item = getItem(user, location, "soup", ItemType.CONSUMABLE, 1, 1);
        em.persist(item);

        //when
        Item findItem = itemRepository.findByItemNoAndUserNo(item.getItemNo(), user.getUserNo()).get();

        //then
        Assertions.assertThat(findItem).isSameAs(item);
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
}
//    @Test
//    void findAll() throws Exception {
//        //given
//        User user = createUser("user1", "username1");
//        Location location = createLocation();
//        Item item1 = getItem(user, location, "item1", ItemType.CONSUMABLE, 2, 1);
//        Item item2 = getItem(user, location, "item2", ItemType.CONSUMABLE, 2, 1);
//        Item item3 = getItem(user, location, "item3", ItemType.CONSUMABLE, 2, 1);
//
//        User anotherUser = createUser("user2", "username2");
//        Item item4 = getItem(anotherUser, location, "item4", ItemType.CONSUMABLE, 2, 1);
//
//        em.persist(item1);
//        em.persist(item2);
//        em.persist(item3);
//        em.persist(item4);
//
//        //when
//        List<Item> items = itemRepository.findAll(user.getUserNo());
//
//        //then
//        Assertions.assertThat(items.size()).isEqualTo(3);
//        Assertions.assertThat(items).containsExactly(item1, item2, item3);
//    }
//}