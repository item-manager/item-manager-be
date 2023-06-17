package com.house.item.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.entity.User;

@Transactional
@SpringBootTest
class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;
	@Autowired
	EntityManager em;

	@DisplayName("id로 User를 조회한다")
	@Test
	void findById() throws Exception {
		//given
		String userId = "user_id";
		User user = createUser(userId, "user1");
		em.persist(user);

		//when
		Optional<User> findUser = userRepository.findById(userId);

		//then
		assertThat(findUser).isNotEmpty();
		assertThat(findUser.get())
			.extracting("id", "username")
			.containsOnly("user_id", "user1");
	}

	@DisplayName("username으로 User를 조회한다")
	@Test
	void findByUsername() throws Exception {
		//given
		String username = "user1";
		User user = createUser("user_id", username);
		em.persist(user);

		//when
		Optional<User> findUser = userRepository.findByUsername(username);

		//then
		assertThat(findUser).isNotEmpty();
		assertThat(findUser.get())
			.extracting("id", "username")
			.containsOnly("user_id", "user1");
	}

	User createUser(String id, String username) {
		return User.builder()
			.id(id)
			.username(username)
			.password("password")
			.salt("salt")
			.build();
	}
}
