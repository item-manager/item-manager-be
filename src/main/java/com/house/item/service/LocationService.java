package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.CreatePlaceRQ;
import com.house.item.domain.CreateRoomRQ;
import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentLocationException;
import com.house.item.exception.NotLocationTypeRoomException;
import com.house.item.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {
    private final AuthService authService;

    private final LocationRepository locationRepository;

    public Long createRoom(CreateRoomRQ createRoomRQ) {
        User loginUser = authService.getLoginUser();

        Location room = Location.builder()
                .user(loginUser)
                .type(LocationType.ROOM)
                .name(createRoomRQ.getName())
                .build();
        return locationRepository.save(room);
    }

    public Long createPlace(CreatePlaceRQ createPlaceRQ) {
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

    private Location checkRoom(CreatePlaceRQ createPlaceRQ, Long loginUserNo) throws NonExistentLocationException, NotLocationTypeRoomException {
        Location room = locationRepository.findByLocationNoAndUserNo(createPlaceRQ.getRoomNo(), loginUserNo)
                .orElseThrow(() -> new NonExistentLocationException(ExceptionCodeMessage.NON_EXISTENT_ROOM.message()));
        if (room.getType() != LocationType.ROOM) {
            throw new NotLocationTypeRoomException(ExceptionCodeMessage.NOT_LOCATION_TYPE_ROOM.message());
        }
        return room;
    }
}
