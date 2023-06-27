package com.house.item.service;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.domain.CreatePlaceRQ;
import com.house.item.domain.CreateRoomRQ;
import com.house.item.domain.UpdatePlaceRQ;
import com.house.item.domain.UpdateRoomRQ;
import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentLocationException;
import com.house.item.repository.UserRepository;

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
        User user = createUser("id");
        em.persist(user);

        CreateRoomRQ createRoomRQ = new CreateRoomRQ();
        ReflectionTestUtils.setField(createRoomRQ, "name", "room1");

        //when
        Long roomNo = locationService.createRoom(createRoomRQ, user);

        //then
        Location findRoom = em.find(Location.class, roomNo);
        Assertions.assertThat(findRoom.getUser().getUserNo()).isEqualTo(user.getUserNo());
        Assertions.assertThat(findRoom.getType()).isEqualTo(LocationType.ROOM);
        Assertions.assertThat(findRoom.getName()).isEqualTo(createRoomRQ.getName());
    }

    @Test
    void 위치_생성() throws Exception {
        //given
        User user = createUser("id");
        em.persist(user);

        Location room = getLocation(user, LocationType.ROOM, "room1", null);
        em.persist(room);
        CreatePlaceRQ createPlaceRQ = new CreatePlaceRQ();
        ReflectionTestUtils.setField(createPlaceRQ, "name", "place1");
        ReflectionTestUtils.setField(createPlaceRQ, "roomNo", room.getLocationNo());

        //when
        Long placeNo = locationService.createPlace(createPlaceRQ, user);

        //then
        Location findPlace = em.find(Location.class, placeNo);
        Assertions.assertThat(findPlace.getUser().getUserNo()).isEqualTo(user.getUserNo());
        Assertions.assertThat(findPlace.getType()).isEqualTo(LocationType.PLACE);
        Assertions.assertThat(findPlace.getName()).isEqualTo(createPlaceRQ.getName());
    }

    @Test
    void 존재하지_않는_room으로_place_생성() throws Exception {
        //given
        User user = createUser("id");
        em.persist(user);

        CreatePlaceRQ createPlaceRQ = new CreatePlaceRQ();
        ReflectionTestUtils.setField(createPlaceRQ, "name", "place1");
        ReflectionTestUtils.setField(createPlaceRQ, "roomNo", 0L);

        //when
        Assertions.assertThatThrownBy(() -> locationService.createPlace(createPlaceRQ, user))
            .isInstanceOf(NonExistentLocationException.class);

        //then
    }

    @Test
    void 다른_회원_room으로_place_생성() throws Exception {
        //given
        User user = createUser("id");
        em.persist(user);

        User anotherUser = createUser("user2");
        em.persist(anotherUser);

        Location room = getLocation(anotherUser, LocationType.ROOM, "room1", null);
        em.persist(room);

        CreatePlaceRQ createPlaceRQ = new CreatePlaceRQ();
        ReflectionTestUtils.setField(createPlaceRQ, "name", "place1");
        ReflectionTestUtils.setField(createPlaceRQ, "roomNo", room.getLocationNo());

        //when
        Assertions.assertThatThrownBy(() -> locationService.createPlace(createPlaceRQ, user))
                .isInstanceOf(NonExistentLocationException.class);

        //then
    }

    @Test
    void 방정보수정() throws Exception {
        //given
        User user = createUser("id");
        em.persist(user);

        Location room = getLocation(user, LocationType.ROOM, "room", null);
        em.persist(room);
        Long locationNo = room.getLocationNo();

        em.flush();
        em.clear();

        UpdateRoomRQ updateRoomRQ = new UpdateRoomRQ();
        ReflectionTestUtils.setField(updateRoomRQ, "name", "new room");

        //when
        locationService.updateRoom(locationNo, updateRoomRQ, user);
        em.flush();
        em.clear();

        //then
        Location findRoom = em.find(Location.class, locationNo);
        Assertions.assertThat(findRoom.getName()).isEqualTo("new room");
    }

    @Test
    void 위치정보수정() throws Exception {
        //given
        User user = createUser("id");
        em.persist(user);

        Location room1 = getLocation(user, LocationType.ROOM, "room1", null);
        Location room2 = getLocation(user, LocationType.ROOM, "room2", null);
        Location place = getLocation(user, LocationType.PLACE, "PLACE", room1);
        em.persist(room1);
        em.persist(room2);
        em.persist(place);
        Long room1LocationNo = room1.getLocationNo();
        Long room2LocationNo = room2.getLocationNo();
        Long placeLocationNo = place.getLocationNo();

        em.flush();
        em.clear();

        UpdatePlaceRQ updatePlaceRQ = new UpdatePlaceRQ();
        ReflectionTestUtils.setField(updatePlaceRQ, "roomNo", room2LocationNo);
        ReflectionTestUtils.setField(updatePlaceRQ, "name", "place");

        //when
        locationService.updatePlace(placeLocationNo, updatePlaceRQ, user);

        em.flush();
        em.clear();

        //then
        Location findPlace = em.find(Location.class, placeLocationNo);
        Assertions.assertThat(findPlace.getRoom().getName()).isEqualTo("room2");
        Assertions.assertThat(findPlace.getName()).isEqualTo("place");
    }

    User createUser(String id) {
        return User.builder()
            .id(id)
            .password("pw")
            .salt("salt")
            .username("name")
            .build();
    }

    Location getLocation(User user, LocationType type, String name, Location room) {
        return Location.builder()
            .user(user)
            .type(type)
                .name(name)
                .room(room)
                .build();
    }

}