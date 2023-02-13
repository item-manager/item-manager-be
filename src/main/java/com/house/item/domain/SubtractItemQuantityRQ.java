package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SubtractItemQuantityRQ {
    private LocalDateTime date;
    private int count;
}
