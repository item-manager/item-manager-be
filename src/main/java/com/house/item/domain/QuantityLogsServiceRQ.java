package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuantityLogsServiceRQ {
	private Long itemNo;
	private QuantityTypeRQ type;
	private Integer year;
	private Integer month;
}
