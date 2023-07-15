package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuantityLogSumByDate {
	private int date;
	private int sum;
}
