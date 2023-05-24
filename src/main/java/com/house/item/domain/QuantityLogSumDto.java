package com.house.item.domain;

import com.house.item.entity.QuantityType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class QuantityLogSumDto {
	private Integer date;
	private QuantityType type;
	private Long sum;
}
