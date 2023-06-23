package com.house.item.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.User;

@Transactional
@SpringBootTest
class LocationRepositoryTest {
	@Autowired
	LocationRepository locationRepository;
	@Autowired
	EntityManager em;

	@DisplayName("room으로 location목록을 조회한다")
	@Test
	void findByRoom() throws Exception {
		//given
		User user1 = createUser("user1");
		User user2 = createUser("user2");
		em.persist(user1);
		em.persist(user2);

		Location room1 = createLocation(user1, LocationType.ROOM, null, "room1");
		Location room2 = createLocation(user2, LocationType.ROOM, null, "room2");
		Location room3 = createLocation(user1, LocationType.ROOM, null, "room3");
		locationRepository.saveAll(List.of(room1, room2, room3));

		Location place1 = createLocation(user1, LocationType.PLACE, room1, "place1");
		Location place2 = createLocation(user2, LocationType.PLACE, room2, "place2");
		Location place3 = createLocation(user2, LocationType.PLACE, room2, "place3");
		Location place4 = createLocation(user1, LocationType.PLACE, room3, "place4");
		Location place5 = createLocation(user1, LocationType.PLACE, room3, "place5");
		Location place6 = createLocation(user2, LocationType.PLACE, room2, "place6");
		locationRepository.saveAll(List.of(place1, place2, place3, place4, place5, place6));

		Location searchRoom = Location.builder()
			.locationNo(room2.getLocationNo())
			.build();

		//when
		List<Location> findLocations = locationRepository.findByRoom(searchRoom);

		//then
		assertThat(findLocations).hasSize(3)
			.extracting("name")
			.containsExactlyInAnyOrder("place2", "place3", "place6");
	}

	Location createLocation(User user, LocationType type, Location room, String name) {
		return Location.builder()
			.user(user)
			.type(type)
			.room(room)
			.name(name)
			.build();
	}

	User createUser(String id) {
		return User.builder()
			.id(id)
			.username("username")
			.password("password")
			.salt("salt")
			.build();
	}

	@DisplayName("type과 user로 Location 목록을 조회한다")
	@Nested
	class FindByTypeAndUser {

		@DisplayName("type이 room인 location 목록을 조회한다")
		@Test
		void findByTypeAndUser1() throws Exception {
			//given
			User user1 = createUser("user1");
			User user2 = createUser("user2");
			em.persist(user1);
			em.persist(user2);

			Location room1 = createLocation(user1, LocationType.ROOM, null, "room1");
			Location room2 = createLocation(user2, LocationType.ROOM, null, "room2");
			Location room3 = createLocation(user1, LocationType.ROOM, null, "room3");
			locationRepository.saveAll(List.of(room1, room2, room3));

			Location place1 = createLocation(user1, LocationType.PLACE, room1, "place1");
			Location place2 = createLocation(user2, LocationType.PLACE, room2, "place2");
			Location place3 = createLocation(user2, LocationType.PLACE, room2, "place3");
			Location place4 = createLocation(user1, LocationType.PLACE, room3, "place4");
			Location place5 = createLocation(user1, LocationType.PLACE, room3, "place5");
			Location place6 = createLocation(user2, LocationType.PLACE, room2, "place6");
			locationRepository.saveAll(List.of(place1, place2, place3, place4, place5, place6));

			LocationType searchType = LocationType.ROOM;
			User searchUser = User.builder()
				.userNo(user1.getUserNo())
				.build();

			//when
			List<Location> findLocations = locationRepository.findByTypeAndUser(searchType, searchUser);

			//then
			assertThat(findLocations).hasSize(2)
				.extracting("type", "user.id", "name")
				.containsExactlyInAnyOrder(
					Tuple.tuple(LocationType.ROOM, "user1", "room1"),
					Tuple.tuple(LocationType.ROOM, "user1", "room3")
				);
		}

		@DisplayName("type이 place인 location 목록을 조회한다")
		@Test
		void findByTypeAndUser2() throws Exception {
			//given
			User user1 = createUser("user1");
			User user2 = createUser("user2");
			em.persist(user1);
			em.persist(user2);

			Location room1 = createLocation(user1, LocationType.ROOM, null, "room1");
			Location room2 = createLocation(user2, LocationType.ROOM, null, "room2");
			Location room3 = createLocation(user1, LocationType.ROOM, null, "room3");
			locationRepository.saveAll(List.of(room1, room2, room3));

			Location place1 = createLocation(user1, LocationType.PLACE, room1, "place1");
			Location place2 = createLocation(user2, LocationType.PLACE, room2, "place2");
			Location place3 = createLocation(user2, LocationType.PLACE, room2, "place3");
			Location place4 = createLocation(user1, LocationType.PLACE, room3, "place4");
			Location place5 = createLocation(user1, LocationType.PLACE, room3, "place5");
			Location place6 = createLocation(user2, LocationType.PLACE, room2, "place6");
			locationRepository.saveAll(List.of(place1, place2, place3, place4, place5, place6));

			LocationType searchType = LocationType.PLACE;
			User searchUser = User.builder()
				.userNo(user1.getUserNo())
				.build();

			//when
			List<Location> findLocations = locationRepository.findByTypeAndUser(searchType, searchUser);

			//then
			assertThat(findLocations).hasSize(3)
				.extracting("type", "user.id", "name")
				.containsExactlyInAnyOrder(
					Tuple.tuple(LocationType.PLACE, "user1", "place1"),
					Tuple.tuple(LocationType.PLACE, "user1", "place4"),
					Tuple.tuple(LocationType.PLACE, "user1", "place5")
				);
		}
	}
}
