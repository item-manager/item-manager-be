package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ConsumableItemsRS {
    private Long itemNo;
    private int priority;
    private String name;
    private LocalDateTime latestConsumeDate;
    private LocalDateTime latestPurchaseDate;
    private int quantity;
    private List<LabelRS> labels;
}
