package com.house.item.web;

import com.house.item.domain.*;
import com.house.item.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingController {

    private final LocationService locationService;

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

    @PostMapping("/places")
    public Result<CreatePlaceRS> createPlace(@RequestBody CreatePlaceRQ createPlaceRQ) {
        Long placeNo = locationService.createPlace(createPlaceRQ);
        CreatePlaceRS createPlaceRS = CreatePlaceRS.builder()
                .placeNo(placeNo)
                .build();

        return Result.<CreatePlaceRS>builder()
                .data(createPlaceRS)
                .build();
    }
}
