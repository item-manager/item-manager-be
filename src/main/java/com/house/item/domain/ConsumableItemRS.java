package com.house.item.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.house.item.entity.ItemLabel;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ConsumableItemRS {
    private Long itemNo;
    private int priority;
    private String name;
    private LocalDateTime latestConsumeDate;
    private LocalDateTime latestPurchaseDate;
    private int quantity;
    private List<LabelRS> labels;
    private String roomName;
    private String placeName;

    public static ConsumableItemRS of(ConsumableItemDTO dto) {
        return ConsumableItemRS.builder()
            .itemNo(dto.getItem().getItemNo())
            .priority(dto.getItem().getPriority())
            .name(dto.getItem().getName())
            .latestConsumeDate(dto.getLatestConsume())
            .latestPurchaseDate(dto.getLatestPurchase())
            .quantity(dto.getItem().getQuantity())
            .labels(
                dto.getItem().getItemLabels().stream()
                    .map(ItemLabel::getLabel)
                    .map(LabelRS::of)
                    .collect(Collectors.toList())
            )
            .roomName(dto.getItem().getLocation().getRoom().getName())
            .placeName(dto.getItem().getLocation().getName())
            .build();
    }
}
