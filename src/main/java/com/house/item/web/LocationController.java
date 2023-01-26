package com.house.item.web;

import com.house.item.domain.*;
import com.house.item.exception.NonExistentRoomException;
import com.house.item.exception.NotLocationTypeRoomException;
import com.house.item.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/rooms")
    public Result<List<RoomsRS>> allRooms() {
        List<RoomsRS> roomsRS = locationService.getRooms();
        return Result.<List<RoomsRS>>builder()
                .data(roomsRS)
                .build();
    }

    @PostMapping("/rooms")
    public Result<CreateRoomRS> createRoom(@RequestBody CreateRoomRQ createRoomRQ) {
        Long roomNo = locationService.createRoom(createRoomRQ);
        CreateRoomRS createRoomRS = CreateRoomRS.builder()
                .roomNo(roomNo)
                .build();

        return Result.<CreateRoomRS>builder()
                .data(createRoomRS)
                .build();
    }

    @GetMapping("/places")
    public Result<List<PlacesRS>> getPlacesByRoomNo(PlacesRQ placesRQ) {
        List<PlacesRS> placesRS = locationService.getPlacesByRoomNo(placesRQ.getRoomNo());
        return Result.<List<PlacesRS>>builder()
                .data(placesRS)
                .build();
    }

    @PostMapping("/places")
    public Result<CreatePlaceRS> createPlace(@RequestBody CreatePlaceRQ createPlaceRQ) throws NonExistentRoomException, NotLocationTypeRoomException {
        Long placeNo = locationService.createPlace(createPlaceRQ);
        CreatePlaceRS createPlaceRS = CreatePlaceRS.builder()
                .placeNo(placeNo)
                .build();

        return Result.<CreatePlaceRS>builder()
                .data(createPlaceRS)
                .build();
    }
}
