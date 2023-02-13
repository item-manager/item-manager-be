package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AddItemQuantityRQ {
    private String mall;
    private LocalDateTime date;
    private int unitPrice;
    private int count;
}
