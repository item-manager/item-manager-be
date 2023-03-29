package com.house.item.repository;

import com.house.item.domain.QuantityLogSearch;
import com.house.item.entity.ItemQuantityLog;

import java.util.List;
import java.util.Optional;

public interface ItemQuantityLogRepository {
    void save(ItemQuantityLog itemQuantityLog);

    Optional<ItemQuantityLog> findByItemQuantityLogNoAndUserNo(Long itemQuantityLogNo, Long userNo);

    List<ItemQuantityLog> findByItemNoAndTypeAndYearAndMonth(QuantityLogSearch quantityLogSearch);

    Long getLogsByItemNoRowCount(QuantityLogSearch quantityLogSearch);

    void delete(ItemQuantityLog itemQuantityLog);
}