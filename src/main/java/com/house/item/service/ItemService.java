package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.common.Props;
import com.house.item.domain.*;
import com.house.item.entity.*;
import com.house.item.exception.*;
import com.house.item.repository.ItemRepository;
import com.house.item.util.FileUtil;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Long createItem(CreateItemRQ createItemRQ) throws NonExistentSessionUserException, NonExistentPlaceException, ServiceException {
        User loginUser = authService.getLoginUser();
        Location location;
        try {
            location = locationService.getLocation(createItemRQ.getLocationNo());
            locationService.checkLocationType(location, LocationType.PLACE);
        } catch (NonExistentLocationException | NotLocationTypePlaceException e) {
            throw new NonExistentPlaceException(ExceptionCodeMessage.NON_EXISTENT_PLACE.message());
        }

        Item item = Item.builder()
                .user(loginUser)
                .name(createItemRQ.getName())
                .type(createItemRQ.getType())
                .location(location)
                .locationMemo(createItemRQ.getLocationMemo())
                .photoName(createItemRQ.getPhotoName())
                .quantity(0)
                .priority(createItemRQ.getPriority())
                .build();

        List<Long> labels = createItemRQ.getLabels();
        for (Long labelNo : labels) {
            Label label = labelService.getLabel(labelNo);

            ItemLabel itemLabel = ItemLabel.builder()
                    .item(item)
                    .label(label)
                    .build();

            item.getItemLabels().add(itemLabel);
        }

        itemRepository.save(item);
        return item.getItemNo();
    }

    public Item getItem(Long itemNo) throws NonExistentItemException {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        return itemRepository.findByItemNoAndUserNo(itemNo, sessionUser.getUserNo())
                .orElseThrow(() -> new NonExistentItemException(ExceptionCodeMessage.NON_EXISTENT_ITEM.message()));
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
                .room(item.getLocation().getRoom().getName())
                .place(item.getLocation().getName())
                .locationMemo(item.getLocationMemo())
                .quantity(item.getQuantity())
                .priority(item.getPriority())
                .labels(labels);

        if (StringUtils.hasText(item.getPhotoName())) {
            itemRSBuilder.photoUrl("/images/" + item.getPhotoName());
        }

        return itemRSBuilder.build();
    }

    public List<Item> getItems() {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        return itemRepository.findAll(sessionUser.getUserNo());
    }

    public List<ItemRS> itemsToItemRSList(List<Item> items) {
        List<ItemRS> itemRSList = new ArrayList<>();
        for (Item item : items) {
            itemRSList.add(itemToItemRS(item));
        }
        return itemRSList;
    }

    public List<ConsumableItemDTO> getConsumableItems(ConsumableSearch consumableSearch) {
        List<ConsumableItemDTO> consumableItemDTOS = itemRepository.findConsumableByNameAndLabel(consumableSearch);

        for (ConsumableItemDTO consumableItemDTO : consumableItemDTOS) {
            List<ItemLabel> itemLabels = consumableItemDTO.getItem().getItemLabels();
            for (ItemLabel itemLabel : itemLabels) {
                itemLabel.getLabel();
            }
        }

        return consumableItemDTOS;
    }

    public ConsumableSearch getConsumableSearch(ConsumableItemsRQ consumableItemsRQ) {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);

        Map<String, String> sortMapping = new HashMap<>();
        sortMapping.put("+", "ASC");
        sortMapping.put("-", "DESC");

        ConsumableSearch.ConsumableSearchBuilder consumableSearchBuilder = ConsumableSearch.builder()
                .userNo(sessionUser.getUserNo())
                .sort(sortMapping.get(consumableItemsRQ.getSort()))
                .page(consumableItemsRQ.getPage())
                .size(consumableItemsRQ.getSize());

        if (consumableItemsRQ.getOrderBy() != null) {
            consumableSearchBuilder.orderBy(consumableItemsRQ.getOrderBy().getColumn());
        }
        if (StringUtils.hasText(consumableItemsRQ.getName())) {
            consumableSearchBuilder.name(consumableItemsRQ.getName());
        }
        if (consumableItemsRQ.getLabelNos() != null && !consumableItemsRQ.getLabelNos().isEmpty()) {
            consumableSearchBuilder.labelNos(consumableItemsRQ.getLabelNos());
        }

        return consumableSearchBuilder.build();
    }

    public Page getConsumableItemsPage(ConsumableSearch consumableSearch) {
        int rowCount = itemRepository.getConsumableRowCount(consumableSearch);

        int size = consumableSearch.getSize();
        int totalPage = rowCount / size;
        if (rowCount % size > 0) {
            totalPage++;
        }

        return Page.builder()
                .totalDataCnt(rowCount)
                .totalPages(totalPage)
                .requestPage(consumableSearch.getPage())
                .requestSize(size)
                .build();
    }

    public List<Item> getEquipmentItems(EquipmentSearch equipmentSearch) {
        List<Item> items = itemRepository.findEquipmentByNameAndLabelAndPlace(equipmentSearch);

        for (Item item : items) {
            List<ItemLabel> itemLabels = item.getItemLabels();
            for (ItemLabel itemLabel : itemLabels) {
                itemLabel.getLabel();
            }
        }
        return items;
    }

    public EquipmentSearch getEquipmentSearch(EquipmentItemsRQ equipmentItemsRQ) {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);

        EquipmentSearch.EquipmentSearchBuilder equipmentSearchBuilder = EquipmentSearch.builder()
                .userNo(sessionUser.getUserNo())
                .page(equipmentItemsRQ.getPage())
                .size(equipmentItemsRQ.getSize());

        if (StringUtils.hasText(equipmentItemsRQ.getName())) {
            equipmentSearchBuilder.name(equipmentItemsRQ.getName());
        }
        if (equipmentItemsRQ.getLabelNos() != null && !equipmentItemsRQ.getLabelNos().isEmpty()) {
            List<Label> labels = new ArrayList<>();

            List<Long> labelNos = equipmentItemsRQ.getLabelNos();
            Label label;
            for (Long labelNo : labelNos) {
                label = labelService.getLabel(labelNo);
                labels.add(label);
            }
            equipmentSearchBuilder.labels(labels);
        }
        if (equipmentItemsRQ.getLocationNo() != null) {
            List<Long> placeNos = new ArrayList<>();

            Location location = locationService.getLocation(equipmentItemsRQ.getLocationNo());
            if (location.getType() == LocationType.PLACE) {
                placeNos.add(location.getLocationNo());
            }
            if (location.getType() == LocationType.ROOM) {
                List<Location> places = locationService.getPlacesByRoomNo(location.getLocationNo());
                for (Location place : places) {
                    placeNos.add(place.getLocationNo());
                }
            }
            equipmentSearchBuilder.placeNos(placeNos);
        }

        return equipmentSearchBuilder.build();
    }

    public Page getEquipmentItemsPage(EquipmentSearch equipmentSearch) {
        int rowCount = itemRepository.getEquipmentRowCount(equipmentSearch);

        int size = equipmentSearch.getSize();
        int totalPage = rowCount / size;
        if (rowCount % size > 0) {
            totalPage++;
        }

        return Page.builder()
                .totalDataCnt(rowCount)
                .totalPages(totalPage)
                .requestPage(equipmentSearch.getPage())
                .requestSize(size)
                .build();
    }

    @Transactional
    public void updateItem(Long itemNo, UpdateItemRQ updateItemRQ) {
        Item item = getItem(itemNo);

        Location location;
        try {
            location = locationService.getLocation(updateItemRQ.getLocationNo());
            locationService.checkLocationType(location, LocationType.PLACE);
        } catch (NonExistentLocationException e) {
            throw new NonExistentPlaceException(ExceptionCodeMessage.NON_EXISTENT_PLACE.message());
        }

        //유효한 label인지 확인
        List<Long> labelNos = updateItemRQ.getLabels();
        for (Long labelNo : labelNos) {
            labelService.getLabel(labelNo);
        }

        String photoDir = props.getDir().getFile();
        if (StringUtils.hasText(item.getPhotoName()) && !item.getPhotoName().equals(updateItemRQ.getPhotoName())) {
            FileUtil.deleteFile(photoDir, item.getPhotoName());
        }

        item.updateItem(
                updateItemRQ.getName(),
                updateItemRQ.getType(),
                location,
                updateItemRQ.getLocationMemo(),
                updateItemRQ.getPhotoName(),
                updateItemRQ.getPriority(),
                updateItemRQ.getLabels()
        );
    }

    public List<Item> getItemsInLocation(Long locationNo) {
        Location location = locationService.getLocation(locationNo);

        List<Item> items = null;
        if (location.getType() == LocationType.PLACE) {
            items = itemRepository.findByPlaceNo(locationNo);
        } else if (location.getType() == LocationType.ROOM) {
            items = itemRepository.findByRoomNo(locationNo);
        } else {
            throw new UndefinedLocationTypeException(ExceptionCodeMessage.UNDEFINED_LOCATION_TYPE.message());
        }

        return items;
    }

    @Transactional
    public void deleteItem(Long itemNo) {
        Item item = getItem(itemNo);

        itemRepository.delete(item);
    }
}
