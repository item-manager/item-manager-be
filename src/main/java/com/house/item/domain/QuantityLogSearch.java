package com.house.item.domain;

import org.springframework.data.domain.Pageable;

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
    private Pageable pageable;
}
