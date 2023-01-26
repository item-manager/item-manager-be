package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.CreateItemRQ;
import com.house.item.entity.Item;
import com.house.item.entity.Location;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentPlaceException;
import com.house.item.exception.NonExistentSessionUserException;
import com.house.item.repository.ItemRepository;
import com.house.item.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final AuthService authService;
    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public Long createItem(CreateItemRQ createItemRQ) throws NonExistentSessionUserException, NonExistentPlaceException {
        User loginUser = authService.getLoginUser();
        Location location = getLocation(createItemRQ.getLocationNo());

        Item item = Item.builder()
                .user(loginUser)
                .name(createItemRQ.getName())
                .type(createItemRQ.getType())
                .location(location)
                .locationMemo(createItemRQ.getLocationMemo())
//                .photoUrl(createItemRQ.getPhoto())
                .quantity(0)
                .priority(createItemRQ.getPriority())
                .build();

        return itemRepository.save(item);
    }

    private Location getLocation(Long locationNo) throws NonExistentPlaceException {
        return locationRepository.findOne(locationNo)
                .orElseThrow(() -> new NonExistentPlaceException(ExceptionCodeMessage.NON_EXISTENT_PLACE.message()));
    }
}
