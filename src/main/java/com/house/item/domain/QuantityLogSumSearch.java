package com.house.item.domain;

import com.house.item.entity.Item;
import com.house.item.entity.QuantityType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuantityLogSumSearch {
	private Item item;
	private Integer year;
	private QuantityType type;
}
