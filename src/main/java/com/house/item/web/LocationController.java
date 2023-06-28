package com.house.item.web;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.CreatePlaceRQ;
import com.house.item.domain.CreatePlaceRS;
import com.house.item.domain.CreateRoomRQ;
import com.house.item.domain.CreateRoomRS;
import com.house.item.domain.ErrorResult;
import com.house.item.domain.PlacesRQ;
import com.house.item.domain.PlacesRS;
import com.house.item.domain.Result;
import com.house.item.domain.RoomsRS;
import com.house.item.domain.UpdatePlaceRQ;
import com.house.item.domain.UpdateRoomRQ;
import com.house.item.entity.Location;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentRoomException;
import com.house.item.exception.NotLocationTypeRoomException;
import com.house.item.service.LocationService;
import com.house.item.util.SessionUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "사용자의 '보관장소(방)' 목록 조회")
    @GetMapping("/rooms")
    public Result<List<RoomsRS>> allRooms() {
        User user = SessionUtils.getSessionUser().toUser();

        List<Location> locations = locationService.getRooms(user);

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
    public Result<CreateRoomRS> createRoom(@Validated @RequestBody CreateRoomRQ createRoomRQ) {
        User user = SessionUtils.getSessionUser().toUser();

        Long roomNo = locationService.createRoom(createRoomRQ, user);

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
    public Result<List<PlacesRS>> getPlacesByRoomNo(@Validated @ModelAttribute PlacesRQ placesRQ) throws NonExistentRoomException, NotLocationTypeRoomException {
        User user = SessionUtils.getSessionUser().toUser();

        List<Location> locations = locationService.getPlacesByRoomNo(placesRQ.getRoomNo(), user);

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
    public Result<CreatePlaceRS> createPlace(@Validated @RequestBody CreatePlaceRQ createPlaceRQ) throws NonExistentRoomException, NotLocationTypeRoomException {
        User user = SessionUtils.getSessionUser().toUser();

        Long placeNo = locationService.createPlace(createPlaceRQ, user);

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
    public Result<Void> patchRoom(@PathVariable Long roomNo, @Validated @RequestBody UpdateRoomRQ updateRoomRQ) {
        User user = SessionUtils.getSessionUser().toUser();

        locationService.updateRoom(roomNo, updateRoomRQ, user);

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
    public Result<Void> patchPlace(@PathVariable Long placeNo, @Validated @RequestBody UpdatePlaceRQ updatePlaceRQ) {
        User user = SessionUtils.getSessionUser().toUser();

        locationService.updatePlace(placeNo, updatePlaceRQ, user);

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
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_LOCATION),
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.UNABLE_TO_DELETE_LOCATION_IN_USE_EXCEPTION),
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.UNDEFINED_LOCATION_TYPE)
                    }
            )
    )
    @Operation(summary = "방/위치 삭제")
    @DeleteMapping("/{locationNo}")
    public Result<Void> deleteLocation(@PathVariable Long locationNo) {
        User user = SessionUtils.getSessionUser().toUser();

        locationService.deleteLocation(locationNo, user);

        return Result.<Void>builder()
            .code(200)
            .message("ok")
            .build();
    }
}
