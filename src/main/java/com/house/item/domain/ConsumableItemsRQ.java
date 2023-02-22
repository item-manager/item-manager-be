package com.house.item.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
public class ConsumableItemsRQ {
    private String name;
    private List<Long> labelNos;
    @Schema(description = "priority(중요도), quantity(수량), latest_purchase_date(최근 구매일), latest_consume_date(최근 사용일), null(생성순)")
    private ConsumableItemsOrderByType orderBy;
    @Schema(description = "+(오름차순), -(내림차순)", defaultValue = "+")
    @Pattern(regexp = "^[+-]?$")
    private String sort;
    @Schema(defaultValue = "1")
    private Integer page;
    @Schema(defaultValue = "10")
    private Integer size;

    public ConsumableItemsRQ(String name, List<Long> labelNos, ConsumableItemsOrderByType orderBy, String sort, Integer page, Integer size) {
        this.name = name;
        this.labelNos = labelNos;
        this.orderBy = orderBy;
        if (sort == null) {
            this.sort = "+";
        } else {
            this.sort = sort;
        }
        if (page == null) {
            this.page = 1;
        } else {
            this.page = page;
        }
        if (size == null) {
            this.size = 10;
        } else {
            this.size = size;
        }
    }
}
