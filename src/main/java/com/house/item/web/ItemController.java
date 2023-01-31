package com.house.item.web;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.*;
import com.house.item.entity.Item;
import com.house.item.exception.NonExistentItemException;
import com.house.item.exception.NonExistentPlaceException;
import com.house.item.exception.NonExistentSessionUserException;
import com.house.item.exception.ServiceException;
import com.house.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM)
                    }
            )
    )
    @Operation(summary = "물품 pk로 조회")
    @GetMapping("/{itemNo}")
    public Result<ItemRS> getItem(@PathVariable Long itemNo) throws NonExistentItemException {
        Item item = itemService.getItem(itemNo);

        ItemRS itemRS = ItemRS.builder()
                .itemNo(item.getItemNo())
                .name(item.getName())
                .type(item.getType())
                .room(item.getLocation().getRoom().getName())
                .place(item.getLocation().getName())
                .locationMemo(item.getLocationMemo())
                .quantity(item.getQuantity())
                .priority(item.getPriority())
                .build();
        return Result.<ItemRS>builder()
                .data(itemRS)
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
    @Operation(summary = "물품 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<CreateItemRS> createItem(@Validated @ModelAttribute CreateItemRQ createItemRQ) throws NonExistentSessionUserException, NonExistentPlaceException, ServiceException {
        Long itemNo = itemService.createItem(createItemRQ);

        CreateItemRS createItemRS = CreateItemRS.builder()
                .itemNo(itemNo)
                .build();
        return Result.<CreateItemRS>builder()
                .data(createItemRS)
                .build();
    }
}
