package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class QuantityLogRS {
    private Long quantityLogNo;
    private QuantityTypeRS type;
    private LocalDateTime date;
    private int count;
    private Integer price;
    private Integer unitPrice;
    private String mall;
}
