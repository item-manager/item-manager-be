package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.common.Props;
import com.house.item.domain.CreateItemRQ;
import com.house.item.domain.ItemRS;
import com.house.item.domain.LabelRS;
import com.house.item.domain.SessionUser;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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

        String photoName = "";
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
        for (Long label : labels) {
            item.getItemLabels().add(ItemLabel.builder()
                    .item(item)
                    .label(Label.builder()
                            .labelNo(label)
                            .build())
                    .build());
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
            labels.add(LabelRS.builder()
                    .labelNo(label.getLabelNo())
                    .name(label.getName())
                    .build());
        }

        return ItemRS.builder()
                .itemNo(item.getItemNo())
                .name(item.getName())
                .type(item.getType())
                .room(item.getLocation().getRoom().getName())
                .place(item.getLocation().getName())
                .locationMemo(item.getLocationMemo())
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

    private String storePhoto(MultipartFile photo) throws ServiceException {
        String photoDir = props.getDir().getPhoto();
        return FileUtil.storeFile(photo, photoDir);
    }
}
