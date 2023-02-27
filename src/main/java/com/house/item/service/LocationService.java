package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.*;
import com.house.item.entity.Item;
import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.User;
import com.house.item.exception.*;
import com.house.item.repository.ItemRepository;
import com.house.item.repository.LocationRepository;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {
    private final AuthService authService;

    private final LocationRepository locationRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Long createRoom(CreateRoomRQ createRoomRQ) {
        User loginUser = authService.getLoginUser();

        Location room = Location.builder()
                .user(loginUser)
                .type(LocationType.ROOM)
                .name(createRoomRQ.getName())
                .build();

        locationRepository.save(room);
        return room.getLocationNo();
    }

    @Transactional
    public Long createPlace(CreatePlaceRQ createPlaceRQ) throws NonExistentRoomException, NotLocationTypeRoomException {
        User loginUser = authService.getLoginUser();
        Location room = getLocation(createPlaceRQ.getRoomNo());
        checkLocationType(room, LocationType.ROOM);

        Location place = Location.builder()
                .user(loginUser)
                .type(LocationType.PLACE)
                .room(room)
                .name(createPlaceRQ.getName())
                .build();

        locationRepository.save(place);
        return place.getLocationNo();
    }

    public void checkLocationType(Location location, LocationType type) throws NotLocationTypeRoomException, NotLocationTypePlaceException, UndefinedLocationTypeException {
        if (location.getType() == type) {
            return;
        }

        if (type == LocationType.ROOM) {
            throw new NotLocationTypeRoomException(ExceptionCodeMessage.NOT_LOCATION_TYPE_ROOM.message());
        }

        if (type == LocationType.PLACE) {
            throw new NotLocationTypePlaceException(ExceptionCodeMessage.NOT_LOCATION_TYPE_PLACE.message());
        }

        throw new UndefinedLocationTypeException(ExceptionCodeMessage.UNDEFINED_LOCATION_TYPE.message());
    }

    public Location getLocation(Long locationNo) throws NonExistentLocationException {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        return locationRepository.findByLocationNoAndUserNo(locationNo, sessionUser.getUserNo())
                .orElseThrow(() -> new NonExistentLocationException(ExceptionCodeMessage.MessageDefine.NON_EXISTENT_LOCATION));
    }

    public List<Location> getRooms() {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        return locationRepository.findByTypeAndUserNo(LocationType.ROOM, sessionUser.getUserNo());
    }

    public List<Location> getPlacesByRoomNo(Long roomNo) throws NonExistentRoomException, NotLocationTypeRoomException {
        Location room = getLocation(roomNo);
        checkLocationType(room, LocationType.ROOM);
        return locationRepository.findByRoom(roomNo);
    }

    @Transactional
    public void updateRoom(Long roomNo, UpdateRoomRQ updateRoomRQ) {
        Location room = getLocation(roomNo);
        room.updateRoom(updateRoomRQ.getName());
    }

    @Transactional
    public void updatePlace(Long placeNo, UpdatePlaceRQ updatePlaceRQ) {
        Location place = getLocation(placeNo);
        Location room = null;
        if (!place.getRoom().getLocationNo().equals(updatePlaceRQ.getRoomNo())) {
            room = Location.builder()
                    .locationNo(updatePlaceRQ.getRoomNo())
                    .build();
        }
        place.updatePlace(room, updatePlaceRQ.getName());
    }

    @Transactional
    public void deleteLocation(Long locationNo) {
        Location location = getLocation(locationNo);

        List<Item> items = null;
        if (location.getType() == LocationType.PLACE) {
            items = itemRepository.findByPlaceNo(locationNo);
        }
        if (location.getType() == LocationType.ROOM) {
            items = itemRepository.findByRoomNo(locationNo);
        }

        if (items == null) {
            throw new UndefinedLocationTypeException(ExceptionCodeMessage.UNDEFINED_LOCATION_TYPE.message());
        }

        if (!items.isEmpty()) {
            throw new UnableToDeleteLocationInUseException(ExceptionCodeMessage.UNABLE_TO_DELETE_LOCATION_IN_USE_EXCEPTION.message());
        }

        locationRepository.delete(location);
    }
}
