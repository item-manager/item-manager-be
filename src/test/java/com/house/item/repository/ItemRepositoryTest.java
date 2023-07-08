package com.house.item.repository;

import static com.house.item.entity.ItemType.*;
import static com.house.item.entity.QuantityType.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.domain.ConsumableItemDTO;
import com.house.item.domain.ConsumableSearch;
import com.house.item.domain.EquipmentSearch;
import com.house.item.entity.Item;
import com.house.item.entity.ItemLabel;
import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.ItemType;
import com.house.item.entity.Label;
import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.QuantityType;
import com.house.item.entity.User;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class ItemRepositoryTest {

	@Autowired
	ItemRepository itemRepository;
	@Autowired
	EntityManager em;

	@DisplayName("User로 Item 목록 조회")
	@Test
	void findByUser() throws Exception {
		//given
		User user1 = createUser("user1");
		User user2 = createUser("user2");
		em.persist(user1);
		em.persist(user2);

		Item item1 = getItem(user1, null, "item1", null, 1, 1);
		Item item2 = getItem(user2, null, "item2", null, 1, 1);
		Item item3 = getItem(user1, null, "item3", null, 1, 1);
		Item item4 = getItem(user1, null, "item4", null, 1, 1);
		Item item5 = getItem(user2, null, "item5", null, 1, 1);
		List<Item> items = List.of(item1, item2, item3, item4, item5);
		itemRepository.saveAll(items);

		User searchUser = User.builder()
			.userNo(user2.getUserNo())
			.build();

		//when
		List<Item> findItems = itemRepository.findByUser(searchUser);

		//then
		assertThat(findItems).hasSize(2)
			.extracting("user.id", "name")
			.containsExactlyInAnyOrder(
				Tuple.tuple("user2", "item2"),
				Tuple.tuple("user2", "item5")
			);
	}

	@DisplayName("Item.location으로 Item 목록 조회")
	@Test
	void findByLocation() throws Exception {
		//given
		Location location1 = createPlace(null, "location1");
		Location location2 = createPlace(null, "location2");
		em.persist(location1);
		em.persist(location2);

		Item item1 = getItem(null, location1, "item1", CONSUMABLE, 1, 1);
		Item item2 = getItem(null, location1, "item2", EQUIPMENT, 1, 1);
		Item item3 = getItem(null, location2, "item3", EQUIPMENT, 1, 1);
		List<Item> items = List.of(item1, item2, item3);
		itemRepository.saveAll(items);

		Location searchLocation = Location.builder()
			.locationNo(location2.getLocationNo())
			.build();

		//when
		List<Item> findItems = itemRepository.findByLocation(searchLocation);

		//then
		assertThat(findItems).hasSize(1)
			.extracting("location.name", "name")
			.containsExactlyInAnyOrder(
				Tuple.tuple("location2", "item3")
			);
	}

	@DisplayName("(Location)Item.location.room으로 Item 목록 조회")
	@Test
	void findByRoom() throws Exception {
		//given
		Location room1 = createRoom("room1");
		Location room2 = createRoom("room2");
		em.persist(room1);
		em.persist(room2);
		Location location1 = createPlace(room1, "location1");
		Location location2 = createPlace(room2, "location2");
		em.persist(location1);
		em.persist(location2);

		Item item1 = getItem(null, location1, "item1", CONSUMABLE, 1, 1);
		Item item2 = getItem(null, location1, "item2", EQUIPMENT, 1, 1);
		Item item3 = getItem(null, location2, "item3", EQUIPMENT, 1, 1);
		em.persist(item1);
		em.persist(item2);
		em.persist(item3);

		Location searchRoom = Location.builder()
			.locationNo(room1.getLocationNo())
			.build();

		//when
		List<Item> items = itemRepository.findByRoom(searchRoom);

		//then
		assertThat(items).hasSize(2)
			.extracting("name", "location.room.name")
			.containsExactlyInAnyOrder(
				Tuple.tuple("item1", "room1"),
				Tuple.tuple("item2", "room1")
			);
	}

	User createUser(String id) {
		User user = User.builder()
			.id(id)
			.password("user1pw")
			.salt("salt")
			.username("username")
			.build();
		return user;
	}

	Location createRoom(String name) {
		Location room = Location.builder()
			.type(LocationType.ROOM)
			.name(name)
			.build();
		return room;
	}

	Location createPlace(Location room, String name) {
		Location place = Location.builder()
			.type(LocationType.PLACE)
			.room(room)
			.name(name)
			.build();
		return place;
	}

	Label createLabel(User user, String name) {
		Label label = Label.builder()
			.user(user)
			.name(name)
			.build();
		return label;
	}

	Item getItem(User user, Location location, String name, ItemType type, int quantity, int priority) {
		Item item = Item.builder()
			.user(user)
			.name(name)
			.type(type)
			.location(location)
			.quantity(quantity)
			.priority(priority)
			.build();
		return item;
	}

	ItemLabel createItemLabel(Item item, Label label) {
		ItemLabel itemLabel = ItemLabel.builder()
			.item(item)
			.label(label)
			.build();
		return itemLabel;
	}

	ItemQuantityLog createQuantityLog(Item item, QuantityType type, LocalDateTime date) {
		ItemQuantityLog quantityLog = ItemQuantityLog.builder()
			.item(item)
			.type(type)
			.date(date)
			.build();
		return quantityLog;
	}

	@DisplayName("User, name, ItemLabel, location으로 EQUIPMENT Item 목록 조회")
	@Nested
	class FindEquipmentByNameAndLabelAndPlace {
		@DisplayName("name, labelNos로 조회")
		@Test
		void findEquipmentByNameAndLabelAndPlace1() throws Exception {
			//given
			User user = createUser("user1");
			em.persist(user);

			Location location1 = createPlace(null, "location1");
			Location location2 = createPlace(null, "location2");
			Location location3 = createPlace(null, "location3");
			em.persist(location1);
			em.persist(location2);
			em.persist(location3);

			Label label1 = createLabel(user, "label1");
			Label label2 = createLabel(user, "label2");
			Label label3 = createLabel(user, "label3");
			em.persist(label1);
			em.persist(label2);
			em.persist(label3);

			Item item1 = getItem(user, location1, "item1", EQUIPMENT, 1, 2);
			Item item2 = getItem(user, location3, "user1_item2", EQUIPMENT, 2, 3);
			Item item3 = getItem(user, location2, "item3", EQUIPMENT, 3, 1);
			Item item4 = getItem(user, location2, "item4(user2)", EQUIPMENT, 3, 2);
			Item item5 = getItem(user, location1, "item5", EQUIPMENT, 3, 1);
			em.persist(item1);
			em.persist(item2);
			em.persist(item3);
			em.persist(item4);
			em.persist(item5);

			ItemLabel itemLabel1 = createItemLabel(item1, label1);
			ItemLabel itemLabel2 = createItemLabel(item1, label2);
			ItemLabel itemLabel3 = createItemLabel(item2, label2);
			ItemLabel itemLabel4 = createItemLabel(item2, label3);
			ItemLabel itemLabel5 = createItemLabel(item3, label3);
			ItemLabel itemLabel6 = createItemLabel(item4, label2);
			ItemLabel itemLabel7 = createItemLabel(item4, label1);
			ItemLabel itemLabel8 = createItemLabel(item4, label3);
			ItemLabel itemLabel9 = createItemLabel(item5, label2);
			em.persist(itemLabel1);
			em.persist(itemLabel2);
			em.persist(itemLabel3);
			em.persist(itemLabel4);
			em.persist(itemLabel5);
			em.persist(itemLabel6);
			em.persist(itemLabel7);
			em.persist(itemLabel8);
			em.persist(itemLabel9);

			Pageable pageable = PageRequest.of(0, 3);
			EquipmentSearch search = EquipmentSearch.builder()
				.userNo(user.getUserNo())
				.name("user")
				.labelNos(List.of(label1.getLabelNo(), label3.getLabelNo()))
				.pageable(pageable)
				.build();

			//when
			Page<Item> items = itemRepository.findEquipmentByNameAndLabelAndPlace(search);

			//then
			assertThat(items).hasSize(1)
				.extracting("user.id", "name")
				.containsExactlyInAnyOrder(
					Tuple.tuple("user1", "item4(user2)")
				);
		}

		@DisplayName("name, placeNos로 조회")
		@Test
		void findEquipmentByNameAndLabelAndPlace2() throws Exception {
			//given
			User user = createUser("user1");
			em.persist(user);

			Location location1 = createPlace(null, "location1");
			Location location2 = createPlace(null, "location2");
			Location location3 = createPlace(null, "location3");
			em.persist(location1);
			em.persist(location2);
			em.persist(location3);

			Label label1 = createLabel(user, "label1");
			Label label2 = createLabel(user, "label2");
			Label label3 = createLabel(user, "label3");
			em.persist(label1);
			em.persist(label2);
			em.persist(label3);

			Item item1 = getItem(user, location1, "item1", EQUIPMENT, 1, 2);
			Item item2 = getItem(user, location3, "user1_item2", EQUIPMENT, 2, 3);
			Item item3 = getItem(user, location2, "item3", EQUIPMENT, 3, 1);
			Item item4 = getItem(user, location2, "item4(user2)", EQUIPMENT, 3, 2);
			Item item5 = getItem(user, location1, "item5", EQUIPMENT, 3, 1);
			em.persist(item1);
			em.persist(item2);
			em.persist(item3);
			em.persist(item4);
			em.persist(item5);

			ItemLabel itemLabel1 = createItemLabel(item1, label1);
			ItemLabel itemLabel2 = createItemLabel(item1, label2);
			ItemLabel itemLabel3 = createItemLabel(item2, label2);
			ItemLabel itemLabel4 = createItemLabel(item2, label3);
			ItemLabel itemLabel5 = createItemLabel(item3, label3);
			ItemLabel itemLabel6 = createItemLabel(item4, label2);
			ItemLabel itemLabel7 = createItemLabel(item4, label1);
			ItemLabel itemLabel8 = createItemLabel(item4, label3);
			ItemLabel itemLabel9 = createItemLabel(item5, label2);
			em.persist(itemLabel1);
			em.persist(itemLabel2);
			em.persist(itemLabel3);
			em.persist(itemLabel4);
			em.persist(itemLabel5);
			em.persist(itemLabel6);
			em.persist(itemLabel7);
			em.persist(itemLabel8);
			em.persist(itemLabel9);

			Pageable pageable = PageRequest.of(0, 3);
			EquipmentSearch search = EquipmentSearch.builder()
				.userNo(user.getUserNo())
				.name("item")
				.placeNos(List.of(location1.getLocationNo(), location3.getLocationNo()))
				.pageable(pageable)
				.build();

			//when
			Page<Item> items = itemRepository.findEquipmentByNameAndLabelAndPlace(search);

			//then
			assertThat(items).hasSize(3)
				.extracting("user.id", "name")
				.containsExactlyInAnyOrder(
					Tuple.tuple("user1", "item1"),
					Tuple.tuple("user1", "user1_item2"),
					Tuple.tuple("user1", "item5")
				);
		}
	}

	@DisplayName("User, name, ItemLabel로 CONSUMABLE Item 목록 조회")
	@Nested
	class FindConsumableByNameAndLabel {
		@DisplayName("name으로만 조회 / latest**로 정렬")
		@Test
		void findConsumableByNameAndLabel1() throws Exception {
			//given
			User user = createUser("user1");
			em.persist(user);

			Label label1 = createLabel(user, "label1");
			Label label2 = createLabel(user, "label2");
			Label label3 = createLabel(user, "label3");
			em.persist(label1);
			em.persist(label2);
			em.persist(label3);

			Item item1 = getItem(user, null, "item1", CONSUMABLE, 1, 2);
			Item item2 = getItem(user, null, "item2", CONSUMABLE, 2, 3);
			Item item3 = getItem(user, null, "item_no3", CONSUMABLE, 3, 1);
			Item item4 = getItem(user, null, "item_no4", CONSUMABLE, 3, 1);
			Item item5 = getItem(user, null, "item5", CONSUMABLE, 3, 1);
			em.persist(item1);
			em.persist(item2);
			em.persist(item3);
			em.persist(item4);
			em.persist(item5);

			ItemLabel itemLabel1 = createItemLabel(item1, label1);
			ItemLabel itemLabel2 = createItemLabel(item1, label2);
			ItemLabel itemLabel3 = createItemLabel(item2, label2);
			ItemLabel itemLabel4 = createItemLabel(item2, label3);
			ItemLabel itemLabel5 = createItemLabel(item3, label3);
			ItemLabel itemLabel6 = createItemLabel(item4, label2);
			ItemLabel itemLabel7 = createItemLabel(item4, label1);
			ItemLabel itemLabel8 = createItemLabel(item4, label3);
			ItemLabel itemLabel9 = createItemLabel(item5, label2);
			em.persist(itemLabel1);
			em.persist(itemLabel2);
			em.persist(itemLabel3);
			em.persist(itemLabel4);
			em.persist(itemLabel5);
			em.persist(itemLabel6);
			em.persist(itemLabel7);
			em.persist(itemLabel8);
			em.persist(itemLabel9);

			LocalDateTime now = LocalDateTime.now();
			ItemQuantityLog quantityLog1 = createQuantityLog(item1, PURCHASE, now.minusDays(25));
			ItemQuantityLog quantityLog2 = createQuantityLog(item1, PURCHASE, now.minusDays(20));
			ItemQuantityLog quantityLog3 = createQuantityLog(item2, PURCHASE, now.minusDays(12));
			ItemQuantityLog quantityLog4 = createQuantityLog(item2, PURCHASE, now.minusDays(10));
			ItemQuantityLog quantityLog5 = createQuantityLog(item3, PURCHASE, now.minusDays(22));
			ItemQuantityLog quantityLog6 = createQuantityLog(item4, PURCHASE, now.minusDays(17));
			ItemQuantityLog quantityLog7 = createQuantityLog(item4, PURCHASE, now.minusDays(4));
			ItemQuantityLog quantityLog8 = createQuantityLog(item5, PURCHASE, now.minusDays(11));

			ItemQuantityLog quantityLog9 = createQuantityLog(item1, CONSUME, now.minusDays(10));
			ItemQuantityLog quantityLog10 = createQuantityLog(item2, CONSUME, now.minusDays(5));
			ItemQuantityLog quantityLog11 = createQuantityLog(item3, CONSUME, now.minusDays(20));
			ItemQuantityLog quantityLog12 = createQuantityLog(item3, CONSUME, now.minusDays(2));
			ItemQuantityLog quantityLog13 = createQuantityLog(item4, CONSUME, now.minusDays(6));
			ItemQuantityLog quantityLog14 = createQuantityLog(item5, CONSUME, now.minusDays(14));
			ItemQuantityLog quantityLog15 = createQuantityLog(item5, CONSUME, now.minusDays(8));
			em.persist(quantityLog1);
			em.persist(quantityLog2);
			em.persist(quantityLog3);
			em.persist(quantityLog4);
			em.persist(quantityLog5);
			em.persist(quantityLog6);
			em.persist(quantityLog7);
			em.persist(quantityLog8);
			em.persist(quantityLog9);
			em.persist(quantityLog10);
			em.persist(quantityLog11);
			em.persist(quantityLog12);
			em.persist(quantityLog13);
			em.persist(quantityLog14);
			em.persist(quantityLog15);

			em.flush();
			em.clear();

			Pageable pageable = PageRequest.of(0, 2, Sort.Direction.DESC, "latestPurchase");
			ConsumableSearch search = ConsumableSearch.builder()
				.userNo(user.getUserNo())
				.name("no")
				.pageable(pageable)
				.build();

			//when
			Page<ConsumableItemDTO> consumables = itemRepository.findConsumableByNameAndLabel(search);

			//then
			ItemQuantityLog findLog5 = em.find(ItemQuantityLog.class, quantityLog5.getItemQuantityLogNo());
			ItemQuantityLog findLog7 = em.find(ItemQuantityLog.class, quantityLog7.getItemQuantityLogNo());
			ItemQuantityLog findLog12 = em.find(ItemQuantityLog.class, quantityLog12.getItemQuantityLogNo());
			ItemQuantityLog findLog13 = em.find(ItemQuantityLog.class, quantityLog13.getItemQuantityLogNo());
			assertThat(consumables).hasSize(2)
				.extracting("item.name", "latestPurchase", "latestConsume")
				.containsExactly(
					Tuple.tuple("item_no4", findLog7.getDate(), findLog13.getDate()),
					Tuple.tuple("item_no3", findLog5.getDate(), findLog12.getDate())
				);
		}

		@DisplayName("labelNos로만 조회 / item.**로 정렬")
		@Test
		void findConsumableByNameAndLabel2() throws Exception {
			//given
			User user = createUser("user1");
			em.persist(user);

			Label label1 = createLabel(user, "label1");
			Label label2 = createLabel(user, "label2");
			Label label3 = createLabel(user, "label3");
			em.persist(label1);
			em.persist(label2);
			em.persist(label3);

			Item item1 = getItem(user, null, "item1", CONSUMABLE, 1, 2);
			Item item2 = getItem(user, null, "item2", CONSUMABLE, 2, 3);
			Item item3 = getItem(user, null, "item_no3", CONSUMABLE, 3, 1);
			Item item4 = getItem(user, null, "item_no4", CONSUMABLE, 3, 1);
			Item item5 = getItem(user, null, "item5", CONSUMABLE, 3, 1);
			em.persist(item1);
			em.persist(item2);
			em.persist(item3);
			em.persist(item4);
			em.persist(item5);

			ItemLabel itemLabel1 = createItemLabel(item1, label1);
			ItemLabel itemLabel2 = createItemLabel(item1, label2);
			ItemLabel itemLabel3 = createItemLabel(item2, label2);
			ItemLabel itemLabel4 = createItemLabel(item2, label3);
			ItemLabel itemLabel5 = createItemLabel(item3, label3);
			ItemLabel itemLabel6 = createItemLabel(item4, label2);
			ItemLabel itemLabel7 = createItemLabel(item4, label1);
			ItemLabel itemLabel8 = createItemLabel(item4, label3);
			ItemLabel itemLabel9 = createItemLabel(item5, label2);
			em.persist(itemLabel1);
			em.persist(itemLabel2);
			em.persist(itemLabel3);
			em.persist(itemLabel4);
			em.persist(itemLabel5);
			em.persist(itemLabel6);
			em.persist(itemLabel7);
			em.persist(itemLabel8);
			em.persist(itemLabel9);

			LocalDateTime now = LocalDateTime.now();
			ItemQuantityLog quantityLog1 = createQuantityLog(item1, PURCHASE, now.minusDays(25));
			ItemQuantityLog quantityLog2 = createQuantityLog(item1, PURCHASE, now.minusDays(20));
			ItemQuantityLog quantityLog3 = createQuantityLog(item2, PURCHASE, now.minusDays(12));
			ItemQuantityLog quantityLog4 = createQuantityLog(item2, PURCHASE, now.minusDays(10));
			ItemQuantityLog quantityLog5 = createQuantityLog(item3, PURCHASE, now.minusDays(22));
			ItemQuantityLog quantityLog6 = createQuantityLog(item4, PURCHASE, now.minusDays(17));
			ItemQuantityLog quantityLog7 = createQuantityLog(item4, PURCHASE, now.minusDays(4));
			ItemQuantityLog quantityLog8 = createQuantityLog(item5, PURCHASE, now.minusDays(11));

			ItemQuantityLog quantityLog9 = createQuantityLog(item1, CONSUME, now.minusDays(10));
			ItemQuantityLog quantityLog10 = createQuantityLog(item2, CONSUME, now.minusDays(5));
			ItemQuantityLog quantityLog11 = createQuantityLog(item3, CONSUME, now.minusDays(20));
			ItemQuantityLog quantityLog12 = createQuantityLog(item3, CONSUME, now.minusDays(2));
			ItemQuantityLog quantityLog13 = createQuantityLog(item4, CONSUME, now.minusDays(6));
			ItemQuantityLog quantityLog14 = createQuantityLog(item5, CONSUME, now.minusDays(14));
			ItemQuantityLog quantityLog15 = createQuantityLog(item5, CONSUME, now.minusDays(8));
			em.persist(quantityLog1);
			em.persist(quantityLog2);
			em.persist(quantityLog3);
			em.persist(quantityLog4);
			em.persist(quantityLog5);
			em.persist(quantityLog6);
			em.persist(quantityLog7);
			em.persist(quantityLog8);
			em.persist(quantityLog9);
			em.persist(quantityLog10);
			em.persist(quantityLog11);
			em.persist(quantityLog12);
			em.persist(quantityLog13);
			em.persist(quantityLog14);
			em.persist(quantityLog15);

			em.flush();
			em.clear();

			Pageable pageable = PageRequest.of(0, 2, Sort.Direction.ASC, "priority");
			ConsumableSearch search = ConsumableSearch.builder()
				.userNo(user.getUserNo())
				.labelNos(List.of(label2.getLabelNo(), label3.getLabelNo()))
				.pageable(pageable)
				.build();

			//when
			Page<ConsumableItemDTO> consumables = itemRepository.findConsumableByNameAndLabel(search);

			//then
			ItemQuantityLog findLog4 = em.find(ItemQuantityLog.class, quantityLog4.getItemQuantityLogNo());
			ItemQuantityLog findLog7 = em.find(ItemQuantityLog.class, quantityLog7.getItemQuantityLogNo());
			ItemQuantityLog findLog10 = em.find(ItemQuantityLog.class, quantityLog10.getItemQuantityLogNo());
			ItemQuantityLog findLog13 = em.find(ItemQuantityLog.class, quantityLog13.getItemQuantityLogNo());
			assertThat(consumables).hasSize(2)
				.extracting("item.name", "latestPurchase", "latestConsume")
				.containsExactly(
					Tuple.tuple("item_no4", findLog7.getDate(), findLog13.getDate()),
					Tuple.tuple("item2", findLog4.getDate(), findLog10.getDate())
				);
		}
	}
}