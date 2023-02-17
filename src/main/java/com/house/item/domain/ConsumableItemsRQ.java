package com.house.item.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
public class ConsumableItemsRQ {
    private String name;
    private List<Long> labelNos;
    @Schema(description = "priority(중요도), quantity(수량), latest_purchase_date(최근 구매일), latest_consume_date(최근 사용일)")
    private ConsumableItemsOrderByType orderBy;
    @Schema(description = "+(오름차순), -(내림차순)", defaultValue = "+")
    @Pattern(regexp = "^[+-]$")
    private String sort = "+";
    @Schema(defaultValue = "1")
    private int page = 1;
    @Schema(defaultValue = "10")
    private int size = 10;
}
