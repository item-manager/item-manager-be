package com.house.item.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EquipmentItemsServiceRQ {
	private String name;
	private List<Long> labelNos;
	private Long locationNo;
}
