package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.*;
import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.User;
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
        return locationRepository.save(room);
    }

    @Transactional
    public Long createPlace(CreatePlaceRQ createPlaceRQ) throws NonExistentRoomException, NotLocationTypeRoomException {
        User loginUser = authService.getLoginUser();
        Location room = checkRoom(createPlaceRQ, loginUser.getUserNo());

        Location place = Location.builder()
                .user(loginUser)
                .type(LocationType.PLACE)
                .room(room)
                .name(createPlaceRQ.getName())
                .build();
        return locationRepository.save(place);
    }

    private Location checkRoom(CreatePlaceRQ createPlaceRQ, Long loginUserNo) throws NonExistentRoomException, NotLocationTypeRoomException {
        Location room = locationRepository.findByLocationNoAndUserNo(createPlaceRQ.getRoomNo(), loginUserNo)
                .orElseThrow(() -> new NonExistentRoomException(ExceptionCodeMessage.NON_EXISTENT_ROOM.message()));
        if (room.getType() != LocationType.ROOM) {
            throw new NotLocationTypeRoomException(ExceptionCodeMessage.NOT_LOCATION_TYPE_ROOM.message());
        }
        return room;
    }

    public List<RoomsRS> getRooms() {
        SessionUser sessionUser = (SessionUser) SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        List<Location> locations = locationRepository.findByTypeAndUserNo(LocationType.ROOM, sessionUser.getUserNo());
        return locations.stream()
                .map(room -> RoomsRS.builder()
                        .roomNo(room.getLocationNo())
                        .name(room.getName())
                        .build()
                ).toList();
    }

    public List<PlacesRS> getPlacesByRoomNo(Long roomNo) {
        List<Location> locations = locationRepository.findByRoom(roomNo);
        return locations.stream()
                .map(place -> PlacesRS.builder()
                        .placeNo(place.getLocationNo())
                        .name(place.getName())
                        .build()
                ).toList();
    }
}
