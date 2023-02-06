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
        Location room = checkRoom(createPlaceRQ.getRoomNo());

        Location place = Location.builder()
                .user(loginUser)
                .type(LocationType.PLACE)
                .room(room)
                .name(createPlaceRQ.getName())
                .build();

        locationRepository.save(place);
        return place.getLocationNo();
    }

    private Location checkRoom(Long roomNo) throws NonExistentRoomException, NotLocationTypeRoomException {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        Location room = locationRepository.findByLocationNoAndUserNo(roomNo, sessionUser.getUserNo())
                .orElseThrow(() -> new NonExistentRoomException(ExceptionCodeMessage.NON_EXISTENT_ROOM.message()));
        if (room.getType() != LocationType.ROOM) {
            throw new NotLocationTypeRoomException(ExceptionCodeMessage.NOT_LOCATION_TYPE_ROOM.message());
        }
        return room;
    }

    public Location getLocation(Long locationNo) throws NonExistentLocationException {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        return locationRepository.findByLocationNoAndUserNo(locationNo, sessionUser.getUserNo())
                .orElseThrow(() -> new NonExistentLocationException(ExceptionCodeMessage.NON_EXISTENT_LOCATION.message()));
    }

    public List<Location> getRooms() {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        return locationRepository.findByTypeAndUserNo(LocationType.ROOM, sessionUser.getUserNo());
    }

    public List<Location> getPlacesByRoomNo(Long roomNo) throws NonExistentRoomException, NotLocationTypeRoomException {
        checkRoom(roomNo);
        return locationRepository.findByRoom(roomNo);
    }
}
