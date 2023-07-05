package com.house.item.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EquipmentSearch {
	private Long userNo;
	private String name;
	private List<Long> labelNos;
	private List<Long> placeNos;
	private Pageable pageable;
}
