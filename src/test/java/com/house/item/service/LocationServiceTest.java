package com.house.item.service;

import com.house.item.domain.CreatePlaceRQ;
import com.house.item.domain.CreateRoomRQ;
import com.house.item.domain.SessionUser;
import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentRoomException;
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
class LocationServiceTest {

    @Autowired
    LocationService locationService;
    @Autowired
    EntityManager em;
    @Autowired
    UserRepository userRepository;

    @Test
    void 방_생성() throws Exception {
        //given
        User sessionUser = createSessionUser();
        CreateRoomRQ createRoomRQ = new CreateRoomRQ();
        ReflectionTestUtils.setField(createRoomRQ, "name", "room1");

        //when
        Long roomNo = locationService.createRoom(createRoomRQ);

        //then
        Location findRoom = em.find(Location.class, roomNo);
        Assertions.assertThat(findRoom.getUser().getUserNo()).isEqualTo(sessionUser.getUserNo());
        Assertions.assertThat(findRoom.getType()).isEqualTo(LocationType.ROOM);
        Assertions.assertThat(findRoom.getName()).isEqualTo(createRoomRQ.getName());
    }

    @Test
    void 위치_생성() throws Exception {
        //given
        User sessionUser = createSessionUser();
        Location room = createRoom(sessionUser);
        CreatePlaceRQ createPlaceRQ = new CreatePlaceRQ();
        ReflectionTestUtils.setField(createPlaceRQ, "name", "place1");
        ReflectionTestUtils.setField(createPlaceRQ, "roomNo", room.getLocationNo());

        //when
        Long placeNo = locationService.createPlace(createPlaceRQ);

        //then
        Location findPlace = em.find(Location.class, placeNo);
        Assertions.assertThat(findPlace.getUser().getUserNo()).isEqualTo(sessionUser.getUserNo());
        Assertions.assertThat(findPlace.getType()).isEqualTo(LocationType.PLACE);
        Assertions.assertThat(findPlace.getName()).isEqualTo(createPlaceRQ.getName());
    }

    @Test
    void 존재하지_않는_room으로_place_생성() throws Exception {
        //given
        User sessionUser = createSessionUser();
        CreatePlaceRQ createPlaceRQ = new CreatePlaceRQ();
        ReflectionTestUtils.setField(createPlaceRQ, "name", "place1");
        ReflectionTestUtils.setField(createPlaceRQ, "roomNo", 0L);

        //when
        Assertions.assertThatThrownBy(() -> locationService.createPlace(createPlaceRQ))
                .isInstanceOf(NonExistentRoomException.class);

        //then
    }

    @Test
    void 다른_회원_room으로_place_생성() throws Exception {
        //given
        User sessionUser = createSessionUser();
        User anotherUser = createAnotherUser();
        Location room = createRoom(anotherUser);

        CreatePlaceRQ createPlaceRQ = new CreatePlaceRQ();
        ReflectionTestUtils.setField(createPlaceRQ, "name", "place1");
        ReflectionTestUtils.setField(createPlaceRQ, "roomNo", room.getLocationNo());

        //when
        Assertions.assertThatThrownBy(() -> locationService.createPlace(createPlaceRQ))
                .isInstanceOf(NonExistentRoomException.class);

        //then
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
        SessionUtils.setAttribute(SessionConst.LOGIN_USER, sessionUser);
        return user;
    }

    User createAnotherUser() {
        User user = User.builder()
                .id("id2")
                .password("pw")
                .salt("salt")
                .username("name2")
                .build();
        Long userNo = userRepository.save(user);
        return user;
    }

    Location createRoom(User user) {
        Location room1 = Location.builder()
                .user(user)
                .type(LocationType.ROOM)
                .name("room1")
                .build();
        em.persist(room1);
        return room1;
    }

}