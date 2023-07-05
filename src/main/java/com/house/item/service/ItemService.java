package com.house.item.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.common.Props;
import com.house.item.domain.ConsumableItemDTO;
import com.house.item.domain.ConsumableItemsServiceRQ;
import com.house.item.domain.ConsumableSearch;
import com.house.item.domain.CreateItemRQ;
import com.house.item.domain.EquipmentItemsServiceRQ;
import com.house.item.domain.EquipmentSearch;
import com.house.item.domain.ItemRS;
import com.house.item.domain.ItemTypeRS;
import com.house.item.domain.LabelRS;
import com.house.item.domain.UpdateItemRQ;
import com.house.item.entity.Item;
import com.house.item.entity.ItemLabel;
import com.house.item.entity.Label;
import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentItemException;
import com.house.item.exception.NonExistentLocationException;
import com.house.item.exception.NonExistentPlaceException;
import com.house.item.exception.NonExistentSessionUserException;
import com.house.item.exception.NotLocationTypePlaceException;
import com.house.item.exception.ServiceException;
import com.house.item.exception.UndefinedLocationTypeException;
import com.house.item.repository.ItemRepository;
import com.house.item.util.FileUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
	private final Props props;
	private final ItemRepository itemRepository;
	private final AuthService authService;
	private final LocationService locationService;
	private final LabelService labelService;

	@Transactional
	public Long createItem(CreateItemRQ createItemRQ, User user) throws
		NonExistentSessionUserException,
		NonExistentPlaceException,
		ServiceException {

		Location location;
		try {
			location = locationService.getLocation(createItemRQ.getLocationNo(), user);
			locationService.checkLocationType(location, LocationType.PLACE);
		} catch (NonExistentLocationException | NotLocationTypePlaceException e) {
			throw new NonExistentPlaceException(ExceptionCodeMessage.NON_EXISTENT_PLACE.message());
		}

		Item item = Item.builder()
			.user(user)
			.name(createItemRQ.getName())
			.type(createItemRQ.getType())
			.location(location)
			.locationMemo(createItemRQ.getLocationMemo())
			.photoName(createItemRQ.getPhotoName())
			.quantity(createItemRQ.getQuantity())
			.priority(createItemRQ.getPriority())
			.memo(createItemRQ.getMemo())
			.build();

		List<Long> labels = createItemRQ.getLabels();
		for (Long labelNo : labels) {
			Label label = labelService.getLabel(labelNo, user);

			ItemLabel itemLabel = ItemLabel.builder()
				.item(item)
				.label(label)
				.build();

			item.getItemLabels().add(itemLabel);
		}

		itemRepository.save(item);
		return item.getItemNo();
	}

	public Item getItem(Long itemNo, User user) throws NonExistentItemException {
		Item item = itemRepository.findById(itemNo)
			.orElseThrow(() -> new NonExistentItemException(ExceptionCodeMessage.NON_EXISTENT_ITEM.message()));

		if (item.getUser().getUserNo().equals(user.getUserNo())) {
			return item;
		}
		throw new NonExistentItemException(ExceptionCodeMessage.NON_EXISTENT_ITEM.message());
	}

	public ItemRS itemToItemRS(Item item) {
		List<LabelRS> labels = new ArrayList<>();
		for (ItemLabel itemLabel : item.getItemLabels()) {
			Label label = itemLabel.getLabel();
			labels.add(
				LabelRS.builder()
					.labelNo(label.getLabelNo())
					.name(label.getName())
					.build()
			);
		}

		ItemRS.ItemRSBuilder itemRSBuilder = ItemRS.builder()
			.itemNo(item.getItemNo())
			.name(item.getName())
			.type(ItemTypeRS.fromType(item.getType()).getName())
			.locationNo(item.getLocation().getLocationNo())
			.room(item.getLocation().getRoom().getName())
			.place(item.getLocation().getName())
			.locationMemo(item.getLocationMemo())
			.quantity(item.getQuantity())
			.priority(item.getPriority())
			.memo(item.getMemo())
			.labels(labels);

		if (StringUtils.hasText(item.getPhotoName())) {
			itemRSBuilder.photoUrl(FileUtils.getImageApiUrl(item.getPhotoName()));
		}

		return itemRSBuilder.build();
	}

	public List<Item> getItems(User user) {
		return itemRepository.findByUser(user);
	}

	public List<ItemRS> itemsToItemRSList(List<Item> items) {
		List<ItemRS> itemRSList = new ArrayList<>();
		for (Item item : items) {
			itemRSList.add(itemToItemRS(item));
		}
		return itemRSList;
	}

	public Page<ConsumableItemDTO> getConsumableItems(ConsumableSearch consumableSearch) {
		Page<ConsumableItemDTO> consumableItemDTOS = itemRepository.findConsumableByNameAndLabel(consumableSearch);

		for (ConsumableItemDTO consumableItemDTO : consumableItemDTOS) {
			List<ItemLabel> itemLabels = consumableItemDTO.getItem().getItemLabels();
			for (ItemLabel itemLabel : itemLabels) {
				itemLabel.getLabel();
			}
		}

		return consumableItemDTOS;
	}

	public ConsumableSearch getConsumableSearch(ConsumableItemsServiceRQ request, Pageable pageable, User user) {
		ConsumableSearch.ConsumableSearchBuilder consumableSearchBuilder = ConsumableSearch.builder()
			.userNo(user.getUserNo())
			.pageable(pageable);

		if (StringUtils.hasText(request.getName())) {
			consumableSearchBuilder.name(request.getName());
		}
		if (request.getLabelNos() != null && !request.getLabelNos().isEmpty()) {
			consumableSearchBuilder.labelNos(request.getLabelNos());
		}

		return consumableSearchBuilder.build();
	}

	public Page<Item> getEquipmentItems(EquipmentSearch equipmentSearch) {
		return itemRepository.findEquipmentByNameAndLabelAndPlace(equipmentSearch);
	}

	public EquipmentSearch getEquipmentSearch(EquipmentItemsServiceRQ request, Pageable pageable, User user) {
		EquipmentSearch.EquipmentSearchBuilder equipmentSearchBuilder = EquipmentSearch.builder()
			.userNo(user.getUserNo())
			.pageable(pageable);

		if (StringUtils.hasText(request.getName())) {
			equipmentSearchBuilder.name(request.getName());
		}
		if (request.getLabelNos() != null && !request.getLabelNos().isEmpty()) {
			equipmentSearchBuilder.labelNos(request.getLabelNos());
		}
		if (request.getLocationNo() != null) {
			List<Long> placeNos = new ArrayList<>();

			Location location = locationService.getLocation(request.getLocationNo(), user);
			if (location.getType() == LocationType.PLACE) {
				placeNos.add(location.getLocationNo());
			}
			if (location.getType() == LocationType.ROOM) {
				List<Location> places = locationService.getPlacesByRoomNo(location.getLocationNo(),
					user);
				for (Location place : places) {
					placeNos.add(place.getLocationNo());
				}
			}
			equipmentSearchBuilder.placeNos(placeNos);
		}

		return equipmentSearchBuilder.build();
	}

	@Transactional
	public void updateItem(Long itemNo, UpdateItemRQ updateItemRQ, User user) {
		Item item = getItem(itemNo, user);

		Location location;
		try {
			location = locationService.getLocation(updateItemRQ.getLocationNo(), user);
			locationService.checkLocationType(location, LocationType.PLACE);
		} catch (NonExistentLocationException e) {
			throw new NonExistentPlaceException(ExceptionCodeMessage.NON_EXISTENT_PLACE.message());
		}

		//유효한 label인지 확인
		List<Long> labelNos = updateItemRQ.getLabels();
		for (Long labelNo : labelNos) {
			labelService.getLabel(labelNo, user);
		}

		String photoDir = props.getDir().getFile();
		if (StringUtils.hasText(item.getPhotoName()) && !item.getPhotoName().equals(updateItemRQ.getPhotoName())) {
			FileUtils.deleteFile(photoDir, item.getPhotoName());
		}

		item.updateItem(
			updateItemRQ.getName(),
			updateItemRQ.getType(),
			location,
			updateItemRQ.getLocationMemo(),
			updateItemRQ.getPhotoName(),
			updateItemRQ.getPriority(),
			updateItemRQ.getMemo(),
			updateItemRQ.getLabels()
		);
	}

	public List<Item> getItemsInLocation(Long locationNo, User user) {
		Location location = locationService.getLocation(locationNo, user);

		List<Item> items = null;
		if (location.getType() == LocationType.PLACE) {
			items = itemRepository.findByLocation(location);
		} else if (location.getType() == LocationType.ROOM) {
			items = itemRepository.findByRoom(location);
		} else {
			throw new UndefinedLocationTypeException(ExceptionCodeMessage.UNDEFINED_LOCATION_TYPE.message());
		}

		return items;
	}

	@Transactional
	public void deleteItem(Long itemNo, User user) {
		Item item = getItem(itemNo, user);

		String photoDir = props.getDir().getFile();
		if (StringUtils.hasText(item.getPhotoName())) {
			FileUtils.deleteFile(photoDir, item.getPhotoName());
		}

		itemRepository.delete(item);
	}
}
