package com.house.item.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class QuantityLogsRQ {
    @NotNull
    private Long itemNo;
    @Schema(description = "purchase(구매), consume(사용), null(전체)")
    private QuantityTypeRQ type;
    private Integer year;
    private Integer month;
    @Schema(description = "date(일자), count(수량), price(단위금액), null(일자)")
    private String orderBy;
    @Schema(description = "+(오름차순), -(내림차순)", defaultValue = "+")
    @Pattern(regexp = "^[+-]?$")
    private String sort;
    @Schema(defaultValue = "1")
    private Integer page;
    @Schema(defaultValue = "10")
    private Integer size;

    public QuantityLogsRQ(Long itemNo, QuantityTypeRQ type, Integer year, Integer month, String orderBy, String sort,
        Integer page, Integer size) {
        this.itemNo = itemNo;
        this.type = type;
        this.year = year;
        this.month = month;
        if (orderBy == null) {
            this.orderBy = "date";
        } else {
            this.orderBy = orderBy;
        }
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
