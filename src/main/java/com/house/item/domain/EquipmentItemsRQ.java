package com.house.item.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class EquipmentItemsRQ {
    private String name;
    private List<Long> labelNos;
    private Long locationNo;
    @Schema(defaultValue = "1")
    private Integer page;
    @Schema(defaultValue = "10")
    private Integer size;

    public EquipmentItemsRQ(String name, List<Long> labelNos, Long locationNo, Integer page, Integer size) {
        this.name = name;
        this.labelNos = labelNos;
        this.locationNo = locationNo;
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
