package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class QuantityLogRS {
    private QuantityTypeRS type;
    private LocalDateTime date;
    private int count;
    private int price;
    private String mall;
}
