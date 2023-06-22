package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class QuantityLogSumByDate {
	private int date;
	private int sum;
}
