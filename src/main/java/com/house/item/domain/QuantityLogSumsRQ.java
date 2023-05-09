package com.house.item.domain;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuantityLogSumsRQ {
	@NotNull
	private Long itemNo;
	@Schema(description = "purchase(구매), consume(사용), null(전체)")
	private QuantityTypeRQ type;
	private Integer year;
}
