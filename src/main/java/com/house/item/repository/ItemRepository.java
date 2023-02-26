package com.house.item.repository;

import com.house.item.domain.ConsumableItemDTO;
import com.house.item.domain.ConsumableSearch;
import com.house.item.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    void save(Item item);

    Optional<Item> findOne(Long itemNo);

    Optional<Item> findByItemNoAndUserNo(Long itemNo, Long userNo);

    List<Item> findAll(Long userNo);

    List<Item> findByPlaceNo(Long placeNo);

    List<Item> findByRoomNo(Long roomNo);

    List<ConsumableItemDTO> findConsumableByNameAndLabel(ConsumableSearch consumableSearch);

    int getConsumableRowCount(ConsumableSearch consumableSearch);
}
