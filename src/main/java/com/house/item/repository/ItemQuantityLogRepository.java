package com.house.item.repository;

import com.house.item.domain.QuantityLogSearch;
import com.house.item.entity.ItemQuantityLog;

import java.util.List;

public interface ItemQuantityLogRepository {
    void save(ItemQuantityLog itemQuantityLog);

    List<ItemQuantityLog> findByItemNoAndTypeAndYearAndMonth(QuantityLogSearch quantityLogSearch);
}
