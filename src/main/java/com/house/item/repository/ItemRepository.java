package com.house.item.repository;

import com.house.item.entity.Item;

import java.util.Optional;

public interface ItemRepository {
    void save(Item item);

    Optional<Item> findOne(Long itemNo);

    Optional<Item> findByItemNoAndUserNo(Long itemNo, Long userNo);
}
