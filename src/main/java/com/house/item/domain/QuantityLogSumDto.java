package com.house.item.domain;

import com.house.item.entity.QuantityType;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class QuantityLogSumDto {
	private Integer date;
	private QuantityType type;
	private Integer sum;

	@QueryProjection
	public QuantityLogSumDto(Integer date, QuantityType type, Integer sum) {
		this.date = date;
		this.type = type;
		this.sum = sum;
	}
}
