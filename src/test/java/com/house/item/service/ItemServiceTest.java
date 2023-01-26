package com.house.item.service;

import com.house.item.domain.CreateItemRQ;
import com.house.item.domain.SessionUser;
import com.house.item.entity.*;
import com.house.item.repository.LocationRepository;
import com.house.item.repository.UserRepository;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    ItemService itemService;
    @Autowired
    EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LocationRepository locationRepository;

    //    @Rollback(value = false)
    @Test
    void 물품추가() throws Exception {
        //given
        User user = createSessionUser();
        Location location = createLocation(user);

        CreateItemRQ createItemRQ = new CreateItemRQ();
        ReflectionTestUtils.setField(createItemRQ, "name", "item1");
        ReflectionTestUtils.setField(createItemRQ, "type", ItemType.CONSUMABLE);
        ReflectionTestUtils.setField(createItemRQ, "locationNo", location.getLocationNo());
        ReflectionTestUtils.setField(createItemRQ, "locationMemo", "location memo");
        ReflectionTestUtils.setField(createItemRQ, "priority", 1);

        //when
        Long itemNo = itemService.createItem(createItemRQ);

        //then
        Item findItem = em.find(Item.class, itemNo);
        Assertions.assertThat(findItem.getQuantity()).isZero();
    }

    User createSessionUser() {
        User user = User.builder()
                .id("id")
                .password("pw")
                .salt("salt")
                .username("name")
                .build();
        Long userNo = userRepository.save(user);

        SessionUser sessionUser = SessionUser.builder()
                .userNo(userNo)
                .username(user.getUsername())
                .build();

//        SessionUser sessionUser = SessionUser.builder()
//                .userNo(1L)
//                .username(user.getUsername())
//                .build();
        SessionUtils.setAttribute(SessionConst.LOGIN_USER, sessionUser);
        return user;
    }

    Location createLocation(User user) {
        Location room = Location.builder()
                .user(user)
                .type(LocationType.ROOM)
                .name("room")
                .build();
        locationRepository.save(room);

        Location place = Location.builder()
                .user(user)
                .type(LocationType.PLACE)
                .room(room)
                .name("place")
                .build();
        locationRepository.save(place);

        return place;
    }
}