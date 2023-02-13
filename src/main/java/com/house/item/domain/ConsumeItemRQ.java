package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConsumeItemRQ {
    private LocalDateTime date;
    private int count;
}
