package com.house.item.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ConsumableItemsOrderByType {
    PRIORITY("priority", "priority"),
    QUANTITY("quantity", "quantity"),
    LATEST_PURCHASE_DATE("latest_purchase_date", "latestPurchase"),
    LATEST_CONSUME_DATE("latest_consume_date", "latestConsume"),
    ;
    private final String code;

    private final String column;

    public static ConsumableItemsOrderByType fromCode(String code) {
        return Arrays.stream(ConsumableItemsOrderByType.values())
                .filter(type -> type.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}