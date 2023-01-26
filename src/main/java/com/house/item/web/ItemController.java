package com.house.item.web;

import com.house.item.domain.CreateItemRQ;
import com.house.item.domain.CreateItemRS;
import com.house.item.domain.Result;
import com.house.item.exception.NonExistentPlaceException;
import com.house.item.exception.NonExistentSessionUserException;
import com.house.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

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
