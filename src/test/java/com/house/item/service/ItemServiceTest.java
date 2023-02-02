package com.house.item.service;

import com.house.item.domain.CreateItemRQ;
import com.house.item.domain.SessionUser;
import com.house.item.entity.*;
import com.house.item.repository.LocationRepository;
import com.house.item.repository.UserRepository;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.FileInputStream;

@SpringBootTest
@Transactional
@Slf4j
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
    void 물품생성() throws Exception {
        //given
        User user = createSessionUser();
        Location location = createLocation(user);

        MultipartFile photo = new MockMultipartFile(
                "fileName",
                "/Users/yurim/Downloads/KakaoTalk_Photo_2023-01-27-16-28-12-3.jpeg",
                "image/jpeg",
                new FileInputStream("/Users/yurim/Downloads/KakaoTalk_Photo_2023-01-27-16-28-12-3.jpeg"));
        CreateItemRQ createItemRQ = new CreateItemRQ(
                "item1",
                ItemType.CONSUMABLE,
                location.getLocationNo(),
                "location memo",
                photo,
                1);

        //when
        Long itemNo = itemService.createItem(createItemRQ);

        //then
        Item findItem = em.find(Item.class, itemNo);
        Assertions.assertThat(findItem.getQuantity()).isZero();
    }
    @Test
    void 물품_pk로_조회() throws Exception {
        //given
        User user = createSessionUser();
        Location location = createLocation(user);
        Item item = getItem(user, location, ItemType.CONSUMABLE, "item1", 2);
        em.persist(item);

        //when
        Item findItem = itemService.getItem(item.getItemNo());

        //then
        Assertions.assertThat(findItem).isSameAs(item);
    }

    User createSessionUser() {
        User user = User.builder()
                .id("id")
                .password("pw")
                .salt("salt")
                .username("name")
                .build();
        userRepository.save(user);

        SessionUser sessionUser = SessionUser.builder()
                .userNo(user.getUserNo())
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

    Item getItem(User user, Location location, ItemType type, String name, int quantity) {
        return Item.builder()
                .user(user)
                .type(type)
                .name(name)
                .location(location)
                .locationMemo("location memo")
                .quantity(quantity)
                .priority(1)
                .build();
    }

}