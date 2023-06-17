package com.house.item.repository;

import java.util.List;

import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumDto;
import com.house.item.domain.QuantityLogSumSearch;
import com.house.item.entity.ItemQuantityLog;

public interface ItemQuantityLogRepositoryCustom {
	List<ItemQuantityLog> findByItemNoAndTypeAndYearAndMonth(QuantityLogSearch quantityLogSearch);

	Long getLogsByItemNoRowCount(QuantityLogSearch quantityLogSearch);

	List<QuantityLogSumDto> sumByDate(QuantityLogSumSearch quantityLogSumSearch);
}
