package com.house.item.domain;

import com.house.item.entity.Item;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ConsumableItemDTO {
    private Item item;
    private LocalDateTime latestPurchase;
    private LocalDateTime latestConsume;

    public ConsumableItemDTO(LocalDateTime latestPurchase, LocalDateTime latestConsume) {
        this.latestPurchase = latestPurchase;
        this.latestConsume = latestConsume;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
