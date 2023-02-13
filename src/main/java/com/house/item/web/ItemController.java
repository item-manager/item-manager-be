package com.house.item.web;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.common.Props;
import com.house.item.domain.*;
import com.house.item.entity.Item;
import com.house.item.exception.NonExistentItemException;
import com.house.item.exception.NonExistentPlaceException;
import com.house.item.exception.NonExistentSessionUserException;
import com.house.item.exception.ServiceException;
import com.house.item.service.ItemService;
import com.house.item.service.QuantityLogService;
import com.house.item.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final Props props;
    private final ItemService itemService;
    private final QuantityLogService quantityLogService;


    @Operation(summary = "물품 목록 조회")
    @GetMapping
    public Result<List<ItemRS>> getItems() {
        List<Item> items = itemService.getItems();
        List<ItemRS> itemRSList = itemService.itemsToItemRSList(items);

        return Result.<List<ItemRS>>builder()
                .data(itemRSList)
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
        ItemRS itemRS = itemService.itemToItemRS(item);

        return Result.<ItemRS>builder()
                .data(itemRS)
                .build();
    }

    @Operation(summary = "물품 사진 조회")
    @GetMapping("/{itemNo}/photo")
    public Resource loadPhoto(@PathVariable Long itemNo) throws ServiceException {
        Item item = itemService.getItem(itemNo);

        String photoDir = props.getDir().getPhoto();
        return FileUtil.getResource(photoDir, item.getPhotoName());
    }

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM),
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_PLACE)
                    }
            )
    )
    @Operation(summary = "물품 정보 수정")
    @PatchMapping(value = "/{itemNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Void> updateItem(@PathVariable Long itemNo, @Validated @ModelAttribute UpdateItemRQ updateItemRQ) {
        itemService.updateItem(itemNo, updateItemRQ);

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
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM)
                    }
            )
    )
    @Operation(summary = "물품 구매")
    @PostMapping("/{itemNo}/purchase")
    public Result<PurchaseItemRS> purchaseItem(@PathVariable Long itemNo, @Validated @RequestBody PurchaseItemRQ purchaseItemRQ) {
        int quantity = quantityLogService.purchaseItem(itemNo, purchaseItemRQ);

        PurchaseItemRS purchaseItemRS = PurchaseItemRS.builder()
                .quantity(quantity)
                .build();
        return Result.<PurchaseItemRS>builder()
                .data(purchaseItemRS)
                .build();
    }

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM),
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.SUBTRACT_COUNT_EXCEEDED_ITEM_QUANTITY_EXCEPTION)
                    }
            )
    )
    @Operation(summary = "물품 사용")
    @PostMapping("/{itemNo}/consume")
    public Result<ConsumeItemRS> consumeItem(@PathVariable Long itemNo, @Validated @RequestBody ConsumeItemRQ consumeItemRQ) {
        int quantity = quantityLogService.consumeItem(itemNo, consumeItemRQ);

        ConsumeItemRS consumeItemRS = ConsumeItemRS.builder()
                .quantity(quantity)
                .build();
        return Result.<ConsumeItemRS>builder()
                .data(consumeItemRS)
                .build();
    }

//    @ApiResponse(
//            responseCode = "400",
//            content = @Content(
//                    schema = @Schema(implementation = ErrorResult.class),
//                    examples = {
//                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM),
//                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_LABEL)
//                    }
//            )
//    )
//    @Operation(summary = "물품에 라벨링")
//    @PostMapping("/labels")
//    public Result<ItemLabelRS> attachLabelToItemRQ(@RequestBody AttachLabelToItemRQ attachLabelToItemRQ) {
//        ItemLabel itemLabel = labelService.attachLabelToItem(attachLabelToItemRQ.getItemNo(), attachLabelToItemRQ.getLabelNo());
//        ItemLabelRS itemLabelRS = ItemLabelRS.builder()
//                .itemNo(itemLabel.getItem().getItemNo())
//                .labelNo(itemLabel.getLabel().getLabelNo())
//                .build();
//
//        return Result.<ItemLabelRS>builder()
//                .data(itemLabelRS)
//                .build();
//    }
//
//    @ApiResponse(
//            responseCode = "400",
//            content = @Content(
//                    schema = @Schema(implementation = ErrorResult.class),
//                    examples = {
//                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM),
//                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_LABEL)
//                    }
//            )
//    )
//    @Operation(summary = "물품에서 라벨 제거")
//    @DeleteMapping("/labels")
//    public Result<Void> detachLabelFromItem(@RequestBody DetachLabelFromItemRQ detachLabelFromItemRQ) {
//        labelService.detachLabelFromItem(detachLabelFromItemRQ.getItemNo(), detachLabelFromItemRQ.getLabelNo());
//
//        return Result.<Void>builder()
//                .code(200)
//                .message("ok")
//                .build();
//    }
}
