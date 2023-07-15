package com.house.item.domain;

import com.house.item.entity.QuantityType;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class QuantityLogSumDTO {
	private Integer date;
	private QuantityType type;
	private Integer sum;

	@Builder
	@QueryProjection
	public QuantityLogSumDTO(Integer date, QuantityType type, Integer sum) {
		this.date = date;
		this.type = type;
		this.sum = sum;
	}
}
