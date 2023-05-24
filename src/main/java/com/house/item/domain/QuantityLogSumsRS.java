package com.house.item.domain;

import java.util.List;
import java.util.Map;

import com.house.item.entity.QuantityType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuantityLogSumsRS {
	private Map<QuantityType, List<QuantityLogSumByDate>> logSumByType;
}
