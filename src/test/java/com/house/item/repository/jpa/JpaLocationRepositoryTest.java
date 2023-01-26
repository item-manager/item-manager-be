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

@SpringBootTest
@Transactional
class JpaLocationRepositoryTest {

    @Autowired
    LocationRepository locationRepository;
    @Autowired
    EntityManager em;

    @Test
    void save() throws Exception {
        //given
        User user = createUser();
        Location location = Location.builder()
                .user(user)
                .type(LocationType.ROOM)
                .name("room1")
                .build();

        //when
        Long locationNo = locationRepository.save(location);

        //then
        Location findRoom = em.find(Location.class, locationNo);
        Assertions.assertThat(findRoom.getUser().getUserNo()).isEqualTo(user.getUserNo());
        Assertions.assertThat(findRoom.getName()).isEqualTo(location.getName());
    }

    @Test
    void findOne() throws Exception {
        //given
        User user = createUser();
        Location room = Location.builder()
                .user(user)
                .type(LocationType.ROOM)
                .name("room1")
                .build();
        Long locationNo = locationRepository.save(room);

        //when
        Location findLocation = locationRepository.findOne(locationNo).get();

        //then
        Assertions.assertThat(findLocation).isSameAs(room);
    }

    @Test
    void findByLocationNoAndUserNo() throws Exception {
        //given
        User user = createUser();
        Location room = Location.builder()
                .user(user)
                .type(LocationType.ROOM)
                .name("room1")
                .build();
        Long roomNo = locationRepository.save(room);

        //when
        Location findLocation = locationRepository.findByLocationNoAndUserNo(roomNo, user.getUserNo()).get();

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

}