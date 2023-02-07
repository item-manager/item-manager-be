package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.CreatePlaceRQ;
import com.house.item.domain.CreateRoomRQ;
import com.house.item.domain.SessionUser;
import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentLocationException;
import com.house.item.exception.NonExistentRoomException;
import com.house.item.exception.NotLocationTypePlaceException;
import com.house.item.exception.NotLocationTypeRoomException;
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

    public void checkLocationType(Location location, LocationType type) throws NotLocationTypeRoomException, NotLocationTypePlaceException {
        if (location.getType() != type) {
            if (type == LocationType.ROOM) {
                throw new NotLocationTypeRoomException(ExceptionCodeMessage.NOT_LOCATION_TYPE_ROOM.message());
            }
            if (type == LocationType.PLACE) {
                throw new NotLocationTypePlaceException(ExceptionCodeMessage.NOT_LOCATION_TYPE_PLACE.message());
            }
        }
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
}
