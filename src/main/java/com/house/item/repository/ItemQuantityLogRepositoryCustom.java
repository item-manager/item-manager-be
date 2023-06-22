package com.house.item.repository;

import java.util.List;

import org.springframework.data.domain.Page;

import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumDto;
import com.house.item.domain.QuantityLogSumSearch;
import com.house.item.entity.ItemQuantityLog;

public interface ItemQuantityLogRepositoryCustom {
	Page<ItemQuantityLog> findByItemNoAndTypeAndYearAndMonth(QuantityLogSearch quantityLogSearch);

	List<QuantityLogSumDto> sumByDate(QuantityLogSumSearch quantityLogSumSearch);
}
