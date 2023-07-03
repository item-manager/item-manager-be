package com.house.item.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.ConsumableItemDTO;
import com.house.item.domain.ConsumableItemRS;
import com.house.item.domain.ConsumableItemsRQ;
import com.house.item.domain.ConsumableSearch;
import com.house.item.domain.ConsumeItemRQ;
import com.house.item.domain.ConsumeItemRS;
import com.house.item.domain.CreateItemRQ;
import com.house.item.domain.CreateItemRQ2;
import com.house.item.domain.CreateItemRS;
import com.house.item.domain.EquipmentItemRS;
import com.house.item.domain.EquipmentItemRS2;
import com.house.item.domain.EquipmentItemsRQ;
import com.house.item.domain.EquipmentSearch;
import com.house.item.domain.ErrorResult;
import com.house.item.domain.ItemNameRS;
import com.house.item.domain.ItemRS;
import com.house.item.domain.ItemsInLocationRQ;
import com.house.item.domain.LabelRS;
import com.house.item.domain.PageRS;
import com.house.item.domain.PurchaseItemRQ;
import com.house.item.domain.PurchaseItemRS;
import com.house.item.domain.Result;
import com.house.item.domain.ResultList;
import com.house.item.domain.UpdateItemRQ;
import com.house.item.domain.UpdateItemRQ2;
import com.house.item.entity.Item;
import com.house.item.entity.ItemLabel;
import com.house.item.entity.Label;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentItemException;
import com.house.item.exception.NonExistentLocationException;
import com.house.item.exception.NonExistentPlaceException;
import com.house.item.exception.NonExistentSessionUserException;
import com.house.item.exception.ServiceException;
import com.house.item.exception.UndefinedLocationTypeException;
import com.house.item.service.ItemService;
import com.house.item.service.LabelService;
import com.house.item.service.QuantityLogService;
import com.house.item.util.SessionUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;
	private final QuantityLogService quantityLogService;
	private final LabelService labelService;

	@Operation(summary = "물품 목록 조회")
	@GetMapping("/items")
	public Result<List<ItemRS>> getItems() {
		User user = SessionUtils.getSessionUser().toUser();

		List<Item> items = itemService.getItems(user);
		List<ItemRS> itemRSList = itemService.itemsToItemRSList(items);

		return Result.<List<ItemRS>>builder()
			.data(itemRSList)
			.build();
	}

	@Operation(summary = "물품 목록 조회")
	@GetMapping("/v2/items")
	public Result<List<ItemRS>> getItems2() {
		User user = SessionUtils.getSessionUser().toUser();

		List<Item> items = itemService.getItems(user);
		List<ItemRS> itemRSList = itemService.itemsToItemRSList2(items);

		return Result.<List<ItemRS>>builder()
			.data(itemRSList)
			.build();
	}

	@ApiResponse(
		responseCode = "400",
		content = @Content(
			schema = @Schema(implementation = ErrorResult.class),
			examples = {
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_PLACE)
			}
		)
	)
	@Operation(summary = "물품 생성")
	@PostMapping("/items")
	public Result<CreateItemRS> createItem(@Validated @RequestBody CreateItemRQ createItemRQ) throws
		NonExistentSessionUserException,
		NonExistentPlaceException,
		ServiceException {
		User user = SessionUtils.getSessionUser().toUser();

		Long itemNo = itemService.createItem(createItemRQ, user);

		CreateItemRS createItemRS = CreateItemRS.builder()
			.itemNo(itemNo)
			.build();
		return Result.<CreateItemRS>builder()
			.data(createItemRS)
			.build();
	}

	@ApiResponse(
		responseCode = "400",
		content = @Content(
			schema = @Schema(implementation = ErrorResult.class),
			examples = {
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_LOCATION)
			}
		)
	)
	@Operation(summary = "물품 생성")
	@PostMapping("/v2/items")
	public Result<CreateItemRS> createItem2(@Validated @RequestBody CreateItemRQ2 createItemRQ) throws
		NonExistentSessionUserException,
		NonExistentLocationException,
		ServiceException {
		User user = SessionUtils.getSessionUser().toUser();

		Long itemNo = itemService.createItem2(createItemRQ, user);

		CreateItemRS createItemRS = CreateItemRS.builder()
			.itemNo(itemNo)
			.build();
		return Result.<CreateItemRS>builder()
			.data(createItemRS)
			.build();
	}

	@ApiResponse(
		responseCode = "400",
		content = @Content(
			schema = @Schema(implementation = ErrorResult.class),
			examples = {
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM)
			}
		)
	)
	@Operation(summary = "물품 pk로 조회")
	@GetMapping("/items/{itemNo}")
	public Result<ItemRS> getItem(@PathVariable Long itemNo) throws NonExistentItemException {
		User user = SessionUtils.getSessionUser().toUser();

		Item item = itemService.getItem(itemNo, user);
		ItemRS itemRS = itemService.itemToItemRS(item);

		return Result.<ItemRS>builder()
			.data(itemRS)
			.build();
	}

	@ApiResponse(
		responseCode = "400",
		content = @Content(
			schema = @Schema(implementation = ErrorResult.class),
			examples = {
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM)
			}
		)
	)
	@Operation(summary = "물품 pk로 조회")
	@GetMapping("/v2/items/{itemNo}")
	public Result<ItemRS> getItem2(@PathVariable Long itemNo) throws NonExistentItemException {
		User user = SessionUtils.getSessionUser().toUser();

		Item item = itemService.getItem(itemNo, user);
		ItemRS itemRS = itemService.itemToItemRS2(item);

		return Result.<ItemRS>builder()
			.data(itemRS)
			.build();
	}

	@ApiResponse(
		responseCode = "400",
		content = @Content(
			schema = @Schema(implementation = ErrorResult.class),
			examples = {
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_LOCATION),
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.UNDEFINED_LOCATION_TYPE)
			}
		)
	)
	@Operation(summary = "방/위치 pk로 조회")
	@GetMapping("/items/location")
	public Result<List<ItemNameRS>> getItemsInLocation(
		@Validated @ModelAttribute ItemsInLocationRQ itemsInLocationRQ) throws
		NonExistentLocationException,
		UndefinedLocationTypeException {
		User user = SessionUtils.getSessionUser().toUser();

		List<Item> items = itemService.getItemsInLocation(itemsInLocationRQ.getLocationNo(), user);

		List<ItemNameRS> itemNameRSList = new ArrayList<>();
		for (Item item : items) {
			ItemNameRS itemNameRS = ItemNameRS.builder()
				.itemNo(item.getItemNo())
				.name(item.getName())
				.build();
			itemNameRSList.add(itemNameRS);
		}

		return Result.<List<ItemNameRS>>builder()
			.data(itemNameRSList)
			.build();
	}

	@Operation(summary = "소모품 목록 조회")
	@GetMapping("/items/consumables")
	public ResultList<ConsumableItemRS> getConsumableItems(
		@Validated @ModelAttribute ConsumableItemsRQ consumableItemsRQ) {
		User user = SessionUtils.getSessionUser().toUser();

		ConsumableSearch consumableSearch = itemService.getConsumableSearch(consumableItemsRQ, user);
		Page<ConsumableItemDTO> consumableItemDTOs = itemService.getConsumableItems(consumableSearch);

		List<ConsumableItemRS> consumableItemRSList = new ArrayList<>();
		for (ConsumableItemDTO consumableItemDTO : consumableItemDTOs) {
			Item item = consumableItemDTO.getItem();

			List<ItemLabel> itemLabels = item.getItemLabels();
			List<Label> labels = new ArrayList<>();
			for (ItemLabel itemLabel : itemLabels) {
				labels.add(itemLabel.getLabel());
			}
			List<LabelRS> labelRSList = labelService.labelToLabelRS(labels);

			consumableItemRSList.add(
				ConsumableItemRS.builder()
					.itemNo(item.getItemNo())
					.priority(item.getPriority())
					.name(item.getName())
					.latestConsumeDate(consumableItemDTO.getLatestConsume())
					.latestPurchaseDate(consumableItemDTO.getLatestPurchase())
					.quantity(item.getQuantity())
					.labels(labelRSList)
					.build()
			);
		}

		PageRS consumableItemsPageRS = PageRS.builder()
			.totalDataCnt((int)consumableItemDTOs.getTotalElements())
			.totalPages(consumableItemDTOs.getTotalPages())
			.requestPage(consumableItemDTOs.getPageable().getPageNumber() + 1)
			.requestSize(consumableItemDTOs.getPageable().getPageSize())
			.build();

		return ResultList.<ConsumableItemRS>builder()
			.page(consumableItemsPageRS)
			.data(consumableItemRSList)
			.build();
	}

	@Operation(summary = "비품 목록 조회")
	@GetMapping("/items/equipments")
	public ResultList<EquipmentItemRS> getEquipmentItems(@Validated @ModelAttribute EquipmentItemsRQ equipmentItemsRQ) {
		User user = SessionUtils.getSessionUser().toUser();

		EquipmentSearch equipmentSearch = itemService.getEquipmentSearch(equipmentItemsRQ, user);
		Page<Item> items = itemService.getEquipmentItems(equipmentSearch);

		List<EquipmentItemRS> equipmentItemRSList = new ArrayList<>();
		for (Item item : items) {
			List<ItemLabel> itemLabels = item.getItemLabels();
			List<Label> labels = new ArrayList<>();
			for (ItemLabel itemLabel : itemLabels) {
				labels.add(itemLabel.getLabel());
			}
			List<LabelRS> labelRSList = labelService.labelToLabelRS(labels);

			equipmentItemRSList.add(
				EquipmentItemRS.builder()
					.itemNo(item.getItemNo())
					.priority(item.getPriority())
					.name(item.getName())
					.roomName(item.getLocation().getRoom().getName())
					.placeName(item.getLocation().getName())
					.locationMemo(item.getLocationMemo())
					.labels(labelRSList)
					.build()
			);
		}

		PageRS equipmentItemsPageRS = PageRS.builder()
			.totalDataCnt((int)items.getTotalElements())
			.totalPages(items.getTotalPages())
			.requestPage(items.getPageable().getPageNumber() + 1)
			.requestSize(items.getPageable().getPageSize())
			.build();

		return ResultList.<EquipmentItemRS>builder()
			.page(equipmentItemsPageRS)
			.data(equipmentItemRSList)
			.build();
	}

	@Operation(summary = "비품 목록 조회")
	@GetMapping("/v2/items/equipments")
	public Result<Page<EquipmentItemRS2>> getEquipmentItems2(
		@Validated @ModelAttribute EquipmentItemsRQ equipmentItemsRQ) {
		User user = SessionUtils.getSessionUser().toUser();

		EquipmentSearch equipmentSearch = itemService.getEquipmentSearch(equipmentItemsRQ, user);
		Page<Item> items = itemService.getEquipmentItems(equipmentSearch);

		return Result.<Page<EquipmentItemRS2>>builder()
			.data(items.map(EquipmentItemRS2::of))
			.build();
	}

	@ApiResponse(
		responseCode = "400",
		content = @Content(
			schema = @Schema(implementation = ErrorResult.class),
			examples = {
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM),
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_PLACE)
			}
		)
	)
	@Operation(summary = "물품 정보 수정")
	@PatchMapping(value = "/items/{itemNo}")
	public Result<Void> updateItem(@PathVariable Long itemNo, @Validated @RequestBody UpdateItemRQ updateItemRQ) {
		User user = SessionUtils.getSessionUser().toUser();

		itemService.updateItem(itemNo, updateItemRQ, user);

		return Result.<Void>builder()
			.code(200)
			.message("ok")
			.build();
	}

	@ApiResponse(
		responseCode = "400",
		content = @Content(
			schema = @Schema(implementation = ErrorResult.class),
			examples = {
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM),
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_LOCATION)
			}
		)
	)
	@Operation(summary = "물품 정보 수정")
	@PatchMapping(value = "/v2/items/{itemNo}")
	public Result<Void> updateItem2(@PathVariable Long itemNo, @Validated @RequestBody UpdateItemRQ2 updateItemRQ) {
		User user = SessionUtils.getSessionUser().toUser();

		itemService.updateItem2(itemNo, updateItemRQ, user);

		return Result.<Void>builder()
			.code(200)
			.message("ok")
			.build();
	}

	@ApiResponse(
		responseCode = "400",
		content = @Content(
			schema = @Schema(implementation = ErrorResult.class),
			examples = {
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM)
			}
		)
	)
	@Operation(summary = "물품 구매")
	@PostMapping("/items/{itemNo}/purchase")
	public Result<PurchaseItemRS> purchaseItem(@PathVariable Long itemNo,
		@Validated @RequestBody PurchaseItemRQ purchaseItemRQ) {
		User user = SessionUtils.getSessionUser().toUser();

		int quantity = quantityLogService.purchaseItem(itemNo, purchaseItemRQ, user);

		PurchaseItemRS purchaseItemRS = PurchaseItemRS.builder()
			.quantity(quantity)
			.build();
		return Result.<PurchaseItemRS>builder()
			.data(purchaseItemRS)
			.build();
	}

	@ApiResponse(
		responseCode = "400",
		content = @Content(
			schema = @Schema(implementation = ErrorResult.class),
			examples = {
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM),
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.SUBTRACT_COUNT_EXCEEDED_ITEM_QUANTITY_EXCEPTION)
			}
		)
	)
	@Operation(summary = "물품 사용")
	@PostMapping("/items/{itemNo}/consume")
	public Result<ConsumeItemRS> consumeItem(@PathVariable Long itemNo,
		@Validated @RequestBody ConsumeItemRQ consumeItemRQ) {
		User user = SessionUtils.getSessionUser().toUser();

		int quantity = quantityLogService.consumeItem(itemNo, consumeItemRQ, user);

		ConsumeItemRS consumeItemRS = ConsumeItemRS.builder()
			.quantity(quantity)
			.build();
		return Result.<ConsumeItemRS>builder()
			.data(consumeItemRS)
			.build();
	}

	@ApiResponse(
		responseCode = "400",
		content = @Content(
			schema = @Schema(implementation = ErrorResult.class),
			examples = {
				@ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM)
			}
		)
	)
	@Operation(summary = "물품 제거")
	@DeleteMapping("/items/{itemNo}")
	public Result<Void> deleteItem(@PathVariable Long itemNo) {
		User user = SessionUtils.getSessionUser().toUser();

		itemService.deleteItem(itemNo, user);

		return Result.<Void>builder()
			.code(200)
			.message("ok")
			.build();
	}
}
