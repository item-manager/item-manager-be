package com.house.item.domain;

import com.house.item.entity.Item;
import com.house.item.entity.QuantityType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuantityLogSearch {
    private Item item;
    private QuantityType type;
    private Integer year;
    private Integer month;
    private String orderBy;
    private String sort;
    private int page;
    private int size;
}
