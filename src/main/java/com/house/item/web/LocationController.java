package com.house.item.web;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.*;
import com.house.item.entity.Location;
import com.house.item.exception.NonExistentRoomException;
import com.house.item.exception.NotLocationTypeRoomException;
import com.house.item.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "사용자의 '보관장소(방)' 목록 조회")
    @GetMapping("/rooms")
    public Result<List<RoomsRS>> allRooms() {
        List<Location> locations = locationService.getRooms();

        List<RoomsRS> roomsRS = locations.stream()
                .map(room -> RoomsRS.builder()
                        .roomNo(room.getLocationNo())
                        .name(room.getName())
                        .build()
                ).toList();
        return Result.<List<RoomsRS>>builder()
                .data(roomsRS)
                .build();
    }

    @Operation(summary = "보관장소(방) 생성")
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

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ROOM),
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NOT_LOCATION_TYPE_ROOM)
                    }
            )
    )
    @Operation(summary = "'보관장소(방)'의 '위치' 목록 조회")
    @GetMapping("/places")
    public Result<List<PlacesRS>> getPlacesByRoomNo(@ModelAttribute PlacesRQ placesRQ) throws NonExistentRoomException, NotLocationTypeRoomException {
        List<Location> locations = locationService.getPlacesByRoomNo(placesRQ.getRoomNo());

        List<PlacesRS> placesRS = locations.stream()
                .map(place -> PlacesRS.builder()
                        .placeNo(place.getLocationNo())
                        .name(place.getName())
                        .build()
                ).toList();
        return Result.<List<PlacesRS>>builder()
                .data(placesRS)
                .build();
    }

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ROOM),
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NOT_LOCATION_TYPE_ROOM)
                    }
            )
    )
    @Operation(summary = "위치 생성")
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

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ROOM)
                    }
            )
    )
    @Operation(summary = "보관장소(방) 정보 수정")
    @PatchMapping("/rooms/{roomNo}")
    public Result<Void> patchRoom(@PathVariable Long roomNo, @RequestBody UpdateRoomRQ updateRoomRQ) {
        locationService.updateRoom(roomNo, updateRoomRQ);

        return Result.<Void>builder()
                .code(200)
                .message("ok")
                .build();
    }

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_PLACE)
                    }
            )
    )
    @Operation(summary = "위치 정보 수정")
    @PatchMapping("/places/{placeNo}")
    public Result<Void> patchPlace(@PathVariable Long placeNo, @RequestBody UpdatePlaceRQ updatePlaceRQ) {
        locationService.updatePlace(placeNo, updatePlaceRQ);

        return Result.<Void>builder()
                .code(200)
                .message("ok")
                .build();
    }
}
