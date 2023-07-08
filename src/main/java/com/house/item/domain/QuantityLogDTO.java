package com.house.item.domain;

import com.house.item.entity.ItemQuantityLog;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class QuantityLogDTO {
	private ItemQuantityLog quantityLog;
	private Integer unitPrice;

	@QueryProjection
	public QuantityLogDTO(ItemQuantityLog quantityLog, Integer unitPrice) {
		this.quantityLog = quantityLog;
		this.unitPrice = unitPrice;
	}
}
