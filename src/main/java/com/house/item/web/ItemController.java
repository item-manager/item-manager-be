package com.house.item.web;

import com.house.item.domain.*;
import com.house.item.entity.Item;
import com.house.item.exception.NonExistentPlaceException;
import com.house.item.exception.NonExistentSessionUserException;
import com.house.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public Result<ItemRS> getItem(@ModelAttribute ItemRQ itemRQ) {
        Item item = itemService.getItem(itemRQ.getItemNo());

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

    @PostMapping
    public Result<CreateItemRS> createItem(@RequestBody CreateItemRQ createItemRQ) throws NonExistentSessionUserException, NonExistentPlaceException {
        Long itemNo = itemService.createItem(createItemRQ);

        CreateItemRS createItemRS = CreateItemRS.builder()
                .itemNo(itemNo)
                .build();
        return Result.<CreateItemRS>builder()
                .data(createItemRS)
                .build();
    }
}
