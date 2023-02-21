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
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final Props props;
    private final ItemRepository itemRepository;
    private final AuthService authService;
    private final LocationService locationService;

    @Transactional
    public Long createItem(CreateItemRQ createItemRQ) throws NonExistentSessionUserException, NonExistentPlaceException, ServiceException {
        User loginUser = authService.getLoginUser();
        Location location;
        try {
            location = locationService.getLocation(createItemRQ.getLocationNo());
            locationService.checkLocationType(location, LocationType.PLACE);
        } catch (NonExistentLocationException e) {
            throw new NonExistentPlaceException(ExceptionCodeMessage.NON_EXISTENT_PLACE.message());
        }

        String photoName = null;
        if (createItemRQ.getPhoto() != null) {
            photoName = storePhoto(createItemRQ.getPhoto());
        }

        Item item = Item.builder()
                .user(loginUser)
                .name(createItemRQ.getName())
                .type(createItemRQ.getType())
                .location(location)
                .locationMemo(createItemRQ.getLocationMemo())
                .photoName(photoName)
                .quantity(0)
                .priority(createItemRQ.getPriority())
                .build();

        List<Long> labels = createItemRQ.getLabels();
        for (Long labelNo : labels) {
            Label label = Label.builder()
                    .labelNo(labelNo)
                    .build();

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

        return ItemRS.builder()
                .itemNo(item.getItemNo())
                .name(item.getName())
                .type(item.getType())
                .room(item.getLocation().getRoom().getName())
                .place(item.getLocation().getName())
                .locationMemo(item.getLocationMemo())
                .photoUrl("/photo/" + item.getPhotoName())
                .quantity(item.getQuantity())
                .priority(item.getPriority())
                .labels(labels)
                .build();
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

    public List<ConsumableItemDTO> getConsumableItems(ConsumableItemsRQ consumableItemsRQ) {
        ConsumableSearch consumableSearch = getConsumableSearch(consumableItemsRQ);

        List<ConsumableItemDTO> consumableItemDTOS = itemRepository.findConsumableByNameAndLabel(consumableSearch);

        for (ConsumableItemDTO consumableItemDTO : consumableItemDTOS) {
            List<ItemLabel> itemLabels = consumableItemDTO.getItem().getItemLabels();
            for (ItemLabel itemLabel : itemLabels) {
                itemLabel.getLabel();
            }
        }

        return consumableItemDTOS;
    }

    private ConsumableSearch getConsumableSearch(ConsumableItemsRQ consumableItemsRQ) {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);

        Map<ConsumableItemsOrderByType, String> orderByMapping = new EnumMap<>(ConsumableItemsOrderByType.class);
        orderByMapping.put(ConsumableItemsOrderByType.priority, "priority");
        orderByMapping.put(ConsumableItemsOrderByType.quantity, "quantity");
        orderByMapping.put(ConsumableItemsOrderByType.latest_purchase_date, "latestPurchase");
        orderByMapping.put(ConsumableItemsOrderByType.latest_consume_date, "latestConsume");

        Map<String, String> sortMapping = new HashMap<>();
        sortMapping.put("+", "ASC");
        sortMapping.put("-", "DESC");

        ConsumableSearch.ConsumableSearchBuilder consumableSearchBuilder = ConsumableSearch.builder()
                .userNo(sessionUser.getUserNo())
                .orderBy(orderByMapping.get(consumableItemsRQ.getOrderBy()))
                .sort(sortMapping.get(consumableItemsRQ.getSort()))
                .page(consumableItemsRQ.getPage())
                .size(consumableItemsRQ.getSize());

        if (StringUtils.hasText(consumableItemsRQ.getName())) {
            consumableSearchBuilder.name(consumableItemsRQ.getName());
        }
        if (consumableItemsRQ.getLabelNos() != null && !consumableItemsRQ.getLabelNos().isEmpty()) {
            consumableSearchBuilder.labelNos(consumableItemsRQ.getLabelNos());
        }

        return consumableSearchBuilder.build();
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

        //사진 변경이 발생했다고 가정
        String photoDir = props.getDir().getPhoto();
        if (StringUtils.hasText(item.getPhotoName())) {
            FileUtil.deleteFile(photoDir, item.getPhotoName());
        }

        String photoName = "";
        if (updateItemRQ.getPhoto() != null) {
            photoName = storePhoto(updateItemRQ.getPhoto());
        }

        item.updateItem(
                updateItemRQ.getName(),
                updateItemRQ.getType(),
                location,
                updateItemRQ.getLocationMemo(),
                photoName,
                updateItemRQ.getPriority(),
                updateItemRQ.getLabels());
    }

    private String storePhoto(MultipartFile photo) throws ServiceException {
        String photoDir = props.getDir().getPhoto();
        return FileUtil.storeFile(photo, photoDir);
    }
}
