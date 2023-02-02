package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.common.Props;
import com.house.item.domain.CreateItemRQ;
import com.house.item.domain.SessionUser;
import com.house.item.entity.Item;
import com.house.item.entity.Location;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentItemException;
import com.house.item.exception.NonExistentPlaceException;
import com.house.item.exception.NonExistentSessionUserException;
import com.house.item.exception.ServiceException;
import com.house.item.repository.ItemRepository;
import com.house.item.repository.LocationRepository;
import com.house.item.util.FileUtil;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final AuthService authService;
    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;
    private final Props props;

    @Transactional
    public Long createItem(CreateItemRQ createItemRQ) throws NonExistentSessionUserException, NonExistentPlaceException, ServiceException {
        User loginUser = authService.getLoginUser();
        Location location = getLocation(createItemRQ.getLocationNo());
        String photoName = storePhoto(createItemRQ.getPhoto());

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

        itemRepository.save(item);
        return item.getItemNo();
    }

    private Location getLocation(Long locationNo) throws NonExistentPlaceException {
        return locationRepository.findOne(locationNo)
                .orElseThrow(() -> new NonExistentPlaceException(ExceptionCodeMessage.NON_EXISTENT_PLACE.message()));
    }

    public Item getItem(Long itemNo) throws NonExistentItemException {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        return itemRepository.findByItemNoAndUserNo(itemNo, sessionUser.getUserNo())
                .orElseThrow(() -> new NonExistentItemException(ExceptionCodeMessage.NON_EXISTENT_ITEM.message()));
    }

    public String storePhoto(MultipartFile photo) throws ServiceException {
        String photoDir = props.getDir().getPhoto();
        return FileUtil.storeFile(photo, photoDir);
    }
}
