package com.house.item.repository.jpa;

import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.User;
import com.house.item.repository.LocationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@SpringBootTest
@Transactional
class JpaLocationRepositoryTest {

    @Autowired
    LocationRepository locationRepository;
    @Autowired
    EntityManager em;

    private static Location getRoom(User user, String name) {
        Location location = Location.builder()
                .user(user)
                .type(LocationType.ROOM)
                .name(name)
                .build();
        return location;
    }

    private static Location getPlace(User user, Location room, String name) {
        Location location = Location.builder()
                .user(user)
                .type(LocationType.PLACE)
                .room(room)
                .name(name)
                .build();
        return location;
    }

    @Test
    void save() throws Exception {
        //given
        User user = createUser();
        Location location = getRoom(user, "room1");

        //when
        locationRepository.save(location);

        //then
        Location findRoom = em.find(Location.class, location.getLocationNo());
        Assertions.assertThat(findRoom.getUser().getUserNo()).isEqualTo(user.getUserNo());
        Assertions.assertThat(findRoom.getName()).isEqualTo(location.getName());
    }

    @Test
    void findOne() throws Exception {
        //given
        User user = createUser();
        Location room = getRoom(user, "room1");
        locationRepository.save(room);

        //when
        Location findLocation = locationRepository.findOne(room.getLocationNo()).get();

        //then
        Assertions.assertThat(findLocation).isSameAs(room);
    }

    @Test
    void findByLocationNoAndUserNo() throws Exception {
        //given
        User user = createUser();
        Location room = getRoom(user, "room1");
        locationRepository.save(room);

        //when
        Location findLocation = locationRepository.findByLocationNoAndUserNo(room.getLocationNo(), user.getUserNo()).get();

        //then
        Assertions.assertThat(findLocation).isSameAs(room);
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

    @Test
    void findByTypeAndUserNo() throws Exception {
        //given
        User user = createUser();
        Location room1 = getRoom(user, "room1");
        Location room2 = getRoom(user, "room2");
        Location place1 = getPlace(user, room1, "place1");
        Location place2 = getPlace(user, room2, "place2");
        Location place3 = getPlace(user, room2, "place3");
        em.persist(room1);
        em.persist(room2);
        em.persist(place1);
        em.persist(place2);
        em.persist(place3);

        //when
        List<Location> rooms = locationRepository.findByTypeAndUserNo(LocationType.ROOM, user.getUserNo());
        List<Location> places = locationRepository.findByTypeAndUserNo(LocationType.PLACE, user.getUserNo());

        //then
        Assertions.assertThat(rooms).containsExactly(room1, room2);
        Assertions.assertThat(places).containsExactly(place1, place2, place3);
    }

    @Test
    void findByRoom() throws Exception {
        //given
        User user = createUser();
        Location room1 = getRoom(user, "room1");
        Location room2 = getRoom(user, "room2");
        Location place1 = getPlace(user, room1, "place1");
        Location place2 = getPlace(user, room2, "place2");
        Location place3 = getPlace(user, room2, "place3");
        em.persist(room1);
        em.persist(room2);
        em.persist(place1);
        em.persist(place2);
        em.persist(place3);

        //when
        List<Location> places = locationRepository.findByRoom(room2.getLocationNo());

        //then
        Assertions.assertThat(places).containsExactly(place2, place3);
    }

}