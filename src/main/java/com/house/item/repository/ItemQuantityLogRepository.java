package com.house.item.repository;

import java.util.List;
import java.util.Optional;

import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumDto;
import com.house.item.domain.QuantityLogSumSearch;
import com.house.item.entity.ItemQuantityLog;

public interface ItemQuantityLogRepository {
    void save(ItemQuantityLog itemQuantityLog);

    Optional<ItemQuantityLog> findByItemQuantityLogNoAndUserNo(Long itemQuantityLogNo, Long userNo);

    List<ItemQuantityLog> findByItemNoAndTypeAndYearAndMonth(QuantityLogSearch quantityLogSearch);

    Long getLogsByItemNoRowCount(QuantityLogSearch quantityLogSearch);

    List<QuantityLogSumDto> sumByDate(QuantityLogSumSearch quantityLogSumSearch);

    void delete(ItemQuantityLog itemQuantityLog);
}