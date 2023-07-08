package com.house.item.repository;

import java.util.List;

import org.springframework.data.domain.Page;

import com.house.item.domain.QuantityLogDTO;
import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumDto;
import com.house.item.domain.QuantityLogSumSearch;

public interface ItemQuantityLogRepositoryCustom {
	Page<QuantityLogDTO> findByItemNoAndTypeAndYearAndMonth(QuantityLogSearch quantityLogSearch);

	List<QuantityLogSumDto> sumByDate(QuantityLogSumSearch quantityLogSumSearch);
}
