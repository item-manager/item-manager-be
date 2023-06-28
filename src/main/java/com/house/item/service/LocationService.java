package com.house.item.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.CreatePlaceRQ;
import com.house.item.domain.CreateRoomRQ;
import com.house.item.domain.UpdatePlaceRQ;
import com.house.item.domain.UpdateRoomRQ;
import com.house.item.entity.Item;
import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentLocationException;
import com.house.item.exception.NonExistentRoomException;
import com.house.item.exception.NotLocationTypePlaceException;
import com.house.item.exception.NotLocationTypeRoomException;
import com.house.item.exception.UnableToDeleteLocationInUseException;
import com.house.item.exception.UndefinedLocationTypeException;
import com.house.item.repository.ItemRepository;
import com.house.item.repository.LocationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {
    private final LocationRepository locationRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Long createRoom(CreateRoomRQ createRoomRQ, User user) {
        Location room = Location.builder()
            .user(user)
            .type(LocationType.ROOM)
            .name(createRoomRQ.getName())
            .build();

        locationRepository.save(room);
        return room.getLocationNo();
    }

    @Transactional
    public Long createPlace(CreatePlaceRQ createPlaceRQ, User user) throws
        NonExistentRoomException,
        NotLocationTypeRoomException {
        Location room = getLocation(createPlaceRQ.getRoomNo(), user);
        checkLocationType(room, LocationType.ROOM);

        Location place = Location.builder()
            .user(user)
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

    public Location getLocation(Long locationNo, User user) throws NonExistentLocationException {
        Location location = locationRepository.findById(locationNo)
            .orElseThrow(
                () -> new NonExistentLocationException(ExceptionCodeMessage.MessageDefine.NON_EXISTENT_LOCATION));
        if (location.getUser().getUserNo().equals(user.getUserNo())) {
            return location;
        }
        throw new NonExistentLocationException(ExceptionCodeMessage.MessageDefine.NON_EXISTENT_LOCATION);
    }

    public List<Location> getRooms(User user) {
        return locationRepository.findByTypeAndUser(LocationType.ROOM, user);
    }

    public List<Location> getPlacesByRoomNo(Long roomNo, User user) throws
        NonExistentRoomException,
        NotLocationTypeRoomException {
        Location room = getLocation(roomNo, user);
        checkLocationType(room, LocationType.ROOM);
        return locationRepository.findByRoom(room);
    }

    @Transactional
    public void updateRoom(Long roomNo, UpdateRoomRQ updateRoomRQ, User user) {
        Location room = getLocation(roomNo, user);
        room.updateRoom(updateRoomRQ.getName());
    }

    @Transactional
    public void updatePlace(Long placeNo, UpdatePlaceRQ updatePlaceRQ, User user) {
        Location place = getLocation(placeNo, user);
        Location room = null;
        if (!place.getRoom().getLocationNo().equals(updatePlaceRQ.getRoomNo())) {
            room = Location.builder()
                .locationNo(updatePlaceRQ.getRoomNo())
                .build();
        }
        place.updatePlace(room, updatePlaceRQ.getName());
    }

    @Transactional
    public void deleteLocation(Long locationNo, User user) {
        Location location = getLocation(locationNo, user);

        List<Item> items = null;
        if (location.getType() == LocationType.PLACE) {
            items = itemRepository.findByLocation(location);
        }
        if (location.getType() == LocationType.ROOM) {
            items = itemRepository.findByRoom(location);
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
