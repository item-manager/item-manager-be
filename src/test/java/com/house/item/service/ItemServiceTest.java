package com.house.item.service;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.common.Props;
import com.house.item.domain.ConsumableItemDTO;
import com.house.item.domain.ConsumableItemsServiceRQ;
import com.house.item.domain.ConsumableSearch;
import com.house.item.domain.CreateItemRQ;
import com.house.item.domain.UpdateItemRQ;
import com.house.item.entity.Item;
import com.house.item.entity.ItemLabel;
import com.house.item.entity.ItemType;
import com.house.item.entity.Label;
import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.User;
import com.house.item.repository.LocationRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class ItemServiceTest {

	@Autowired
	ItemService itemService;
	@Autowired
	EntityManager em;
	@Autowired
	LocationRepository locationRepository;
	@Autowired
	Props props;

	@Test
	void 물품생성() throws Exception {
		//given
		User user = createUser("id");
		em.persist(user);

		Location room = createRoom(user, "room");
		Location location = createPlace(user, room, "place");
		Label label1 = createLabel(user, "label1");
		Long label1No = label1.getLabelNo();
		Label label2 = createLabel(user, "label2");
		Long label2No = label2.getLabelNo();

		CreateItemRQ createItemRQ = new CreateItemRQ();
		ReflectionTestUtils.setField(createItemRQ, "name", "item1");
		ReflectionTestUtils.setField(createItemRQ, "type", ItemType.CONSUMABLE);
		ReflectionTestUtils.setField(createItemRQ, "locationNo", location.getLocationNo());
		ReflectionTestUtils.setField(createItemRQ, "quantity", 3);
		ReflectionTestUtils.setField(createItemRQ, "priority", 1);
		ReflectionTestUtils.setField(createItemRQ, "labels", new ArrayList<>(List.of(label1No, label2No)));

		//when
		Long itemNo = itemService.createItem(createItemRQ, user);

		//then
		Item findItem = em.find(Item.class, itemNo);
		assertThat(findItem.getItemNo()).isNotNull();
	}

	@Test
	void 물품_pk로_조회() throws Exception {
		//given
		User user = createUser("id");
		em.persist(user);

		Location room = createRoom(user, "room");
		Location location = createPlace(user, room, "place");
		Item item = getItem(location, ItemType.CONSUMABLE, "item1", 2, 1);
		em.persist(item);

		//when
		Item findItem = itemService.getItem(item.getItemNo(), user);

		//then
		assertThat(findItem).isSameAs(item);
	}

	@Test
	void user_pk로_item_목록조회() throws Exception {
		//given
		User user = createUser("id");
		em.persist(user);

		Location room = createRoom(user, "room");
		Location location = createPlace(user, room, "place");
		Item item1 = getItem(location, ItemType.CONSUMABLE, "item1", 2, 1);
		Item item2 = getItem(location, ItemType.CONSUMABLE, "item2", 2, 1);
		Item item3 = getItem(location, ItemType.CONSUMABLE, "item3", 2, 1);
		em.persist(item1);
		em.persist(item2);
		em.persist(item3);

		//when
		List<Item> items = itemService.getItems(user);

		//then
		assertThat(items).hasSize(3)
			.containsExactly(item1, item2, item3);
	}

	@Test
	void place_pk로_item_목록조회() throws Exception {
		//given
		User user = createUser("id");
		em.persist(user);

		Location room = createRoom(user, "room");
		Location place1 = createPlace(user, room, "place1");
		Location place2 = createPlace(user, room, "place2");
		Item item1 = getItem(place1, ItemType.CONSUMABLE, "item1", 2, 1);
		Item item2 = getItem(place1, ItemType.CONSUMABLE, "item2", 2, 1);
		Item item3 = getItem(place2, ItemType.CONSUMABLE, "item3", 2, 1);
		em.persist(item1);
		em.persist(item2);
		em.persist(item3);

		//when
		List<Item> items = itemService.getItemsInLocation(place1.getLocationNo(), user);

		//then
		assertThat(items)
			.containsExactly(item1, item2)
			.doesNotContain(item3);
	}

	@Test
	void room_pk로_item_목록조회() throws Exception {
		//given
		User user = createUser("id");
		em.persist(user);

		Location room1 = createRoom(user, "room1");
		Location room2 = createRoom(user, "room2");
		Location place1 = createPlace(user, room1, "place1");
		Location place2 = createPlace(user, room2, "place2");
		Item item1 = getItem(place1, ItemType.CONSUMABLE, "item1", 2, 1);
		Item item2 = getItem(place1, ItemType.CONSUMABLE, "item2", 2, 1);
		Item item3 = getItem(place2, ItemType.CONSUMABLE, "item3", 2, 1);
		em.persist(item1);
		em.persist(item2);
		em.persist(item3);

		//when
		List<Item> items = itemService.getItemsInLocation(room1.getLocationNo(), user);

		//then
		assertThat(items)
			.containsExactly(item1, item2)
			.doesNotContain(item3);
	}

	@Test
	void 소모품_검색() throws Exception {
		//given
		User user = createUser("id");
		em.persist(user);

		Location room = createRoom(user, "room");
		Location location = createPlace(user, room, "place");
		Label label1 = createLabel(user, "label1");
		Label label2 = createLabel(user, "label2");

		Item item1 = getItem(location, ItemType.CONSUMABLE, "item1", 1, 3);
		Item item2 = getItem(location, ItemType.CONSUMABLE, "item2", 2, 2);
		Item item3 = getItem(location, ItemType.CONSUMABLE, "item3", 3, 1);
		item1.getItemLabels().add(ItemLabel.builder()
			.item(item1)
			.label(Label.builder()
				.labelNo(label1.getLabelNo())
				.build())
			.build());
		item1.getItemLabels().add(ItemLabel.builder()
			.item(item1)
			.label(Label.builder()
				.labelNo(label2.getLabelNo())
				.build())
			.build());
		item3.getItemLabels().add(ItemLabel.builder()
			.item(item3)
			.label(Label.builder()
				.labelNo(label1.getLabelNo())
				.build())
			.build());
		item3.getItemLabels().add(ItemLabel.builder()
			.item(item3)
			.label(Label.builder()
				.labelNo(label2.getLabelNo())
				.build())
			.build());
		em.persist(item1);
		em.persist(item2);
		em.persist(item3);

		em.flush();
		em.clear();

		//        ConsumableItemsRQ consumableItemsRQ = new ConsumableItemsRQ("item", List.of(label1.getLabelNo(), label2.getLabelNo()), ConsumableItemsOrderByType.PRIORITY, "-", 1, 2);
		Pageable pageable = PageRequest.of(0, 10);
		ConsumableItemsServiceRQ request = ConsumableItemsServiceRQ.builder()
			.name("item")
			.labelNos(List.of(label1.getLabelNo(), label2.getLabelNo()))
			.build();
		ConsumableSearch consumableSearch = itemService.getConsumableSearch(request, pageable, user);

		//when
		Page<ConsumableItemDTO> consumableItems = itemService.getConsumableItems(consumableSearch);

		//then
		assertThat(consumableItems).hasSize(2);
	}

	@Test
	void 소모품검색조건_생성() throws Exception {
		//given
		User user = createUser("id");
		em.persist(user);

		Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "latestConsume");
		ConsumableItemsServiceRQ request = ConsumableItemsServiceRQ.builder()
			.name("name")
			.labelNos(List.of(1L, 2L))
			.build();
		//when
		ConsumableSearch search = itemService.getConsumableSearch(request, pageable, user);

		//then
		assertThat(search.getUserNo()).isEqualTo(user.getUserNo());
		assertThat(search.getName()).isEqualTo("name");
		assertThat(search.getLabelNos()).hasSize(2);
		assertThat(search.getPageable().getSort().stream().findAny().get().getProperty())
			.isEqualTo("latestConsume");
		assertThat(search.getPageable().getSort().stream().findAny().get().isDescending()).isTrue();
		assertThat(search.getPageable().getPageNumber()).isZero();
		assertThat(search.getPageable().getPageSize()).isEqualTo(5);
	}

	@Test
	void 물품정보수정() throws Exception {
		//given
		User user = createUser("id");
		em.persist(user);

		Location room = createRoom(user, "room");
		Location location = createPlace(user, room, "place");
		Location location2 = createPlace(user, room, "place2");
		Label label1 = createLabel(user, "label1");
		Label label2 = createLabel(user, "label2");
		Item item1 = getItem(location, ItemType.CONSUMABLE, "item1", 2, 1);
		item1.getItemLabels().add(
			ItemLabel.builder()
				.item(item1)
				.label(
					Label.builder()
						.labelNo(label1.getLabelNo())
						.build()
				)
				.build()
		);
		em.persist(item1);
		Long itemNo = item1.getItemNo();

		em.flush();
		em.clear();

		UpdateItemRQ updateItemRQ = new UpdateItemRQ();
		ReflectionTestUtils.setField(updateItemRQ, "name", "new item");
		ReflectionTestUtils.setField(updateItemRQ, "type", ItemType.EQUIPMENT);
		ReflectionTestUtils.setField(updateItemRQ, "locationNo", location2.getLocationNo());
		ReflectionTestUtils.setField(updateItemRQ, "photoName", null);
		ReflectionTestUtils.setField(updateItemRQ, "priority", 3);
		ReflectionTestUtils.setField(updateItemRQ, "labels", List.of(label2.getLabelNo()));

		//when
		itemService.updateItem(itemNo, updateItemRQ, user);
		em.flush();
		em.clear();

		//then
		Item findItem = em.find(Item.class, itemNo);
		assertThat(findItem.getName()).isEqualTo("new item");
		assertThat(findItem.getType()).isEqualTo(ItemType.EQUIPMENT);
		assertThat(findItem.getLocation().getName()).isEqualTo("place2");
		assertThat(findItem.getPriority()).isEqualTo(3);
		for (ItemLabel itemLabel : findItem.getItemLabels()) {
			assertThat(itemLabel.getLabel().getLabelNo()).isEqualTo(label2.getLabelNo());
		}
	}

	User createUser(String id) {
		return User.builder()
			.id(id)
			.password("pw")
			.salt("salt")
			.username("name")
			.build();
	}

	Location createRoom(User user, String name) {
		Location room = Location.builder()
			.user(user)
			.type(LocationType.ROOM)
			.name(name)
			.build();
		locationRepository.save(room);

		return room;
	}

	Location createPlace(User user, Location room, String name) {
		Location place = Location.builder()
			.user(user)
			.type(LocationType.PLACE)
			.room(room)
			.name(name)
			.build();
		locationRepository.save(place);

		return place;
	}

	private Label createLabel(User user, String name) {
		Label label = Label.builder()
			.user(user)
			.name(name)
			.build();
		em.persist(label);
		return label;
	}

	Item getItem(Location location, ItemType type, String name, int quantity, int priority) {
		return Item.builder()
			.type(type)
			.name(name)
			.location(location)
			.quantity(quantity)
			.priority(priority)
			.build();
	}

}