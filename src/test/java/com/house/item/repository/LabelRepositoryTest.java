package com.house.item.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.entity.Label;
import com.house.item.entity.User;

@SpringBootTest
@Transactional
class LabelRepositoryTest {

	@Autowired
	private LabelRepository labelRepository;
	@Autowired
	private EntityManager em;

	@DisplayName("user로 Label 목록 조회")
	@Test
	void findByUser() throws Exception {
		//given
		User user1 = createUser("user1");
		User user2 = createUser("user2");
		em.persist(user1);
		em.persist(user2);

		Label label1 = createLabel(user1, "label1");
		Label label2 = createLabel(user1, "label2");
		Label label3 = createLabel(user2, "label3");
		Label label4 = createLabel(user1, "label4");
		Label label5 = createLabel(user2, "label5");
		Label label6 = createLabel(user2, "label6");
		Label label7 = createLabel(user1, "label7");
		labelRepository.saveAll(List.of(label1, label2, label3, label4, label5, label6, label7));

		User searchUser = User.builder()
			.userNo(user2.getUserNo())
			.build();

		//when
		List<Label> findLabels = labelRepository.findByUser(searchUser);

		//then
		assertThat(findLabels).hasSize(3)
			.extracting("user.id", "name")
			.containsExactlyInAnyOrder(
				Tuple.tuple("user2", "label3"),
				Tuple.tuple("user2", "label5"),
				Tuple.tuple("user2", "label6")
			);
	}

	@DisplayName("user와 name으로 Label을 조회한다")
	@Test
	void findByNameAndUser() throws Exception {
		//given
		User user1 = createUser("user1");
		User user2 = createUser("user2");
		em.persist(user1);
		em.persist(user2);

		Label label1 = createLabel(user1, "label1");
		Label label2 = createLabel(user1, "label2");
		Label label3 = createLabel(user2, "label3");
		Label label4 = createLabel(user1, "label4");
		Label label5 = createLabel(user2, "label5");
		Label label6 = createLabel(user2, "label");
		Label label7 = createLabel(user1, "label");
		labelRepository.saveAll(List.of(label1, label2, label3, label4, label5, label6, label7));

		User searchUser = User.builder()
			.userNo(user1.getUserNo())
			.build();
		String searchName = "label";

		//when
		List<Label> findLabels = labelRepository.findByNameAndUser(searchName, searchUser);

		//then
		assertThat(findLabels).hasSize(1)
			.extracting("user.id", "name")
			.containsExactly(Tuple.tuple("user1", "label"));
	}

	User createUser(String id) {
		return User.builder()
			.id(id)
			.password("password")
			.salt("salt")
			.username("username")
			.build();
	}

	Label createLabel(User user, String name) {
		return Label.builder()
			.user(user)
			.name(name)
			.build();
	}
}