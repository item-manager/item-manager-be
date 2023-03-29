package com.house.item.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum QuantityLogsOrderByType {
    DATE("date", "l.date"),
    COUNT("count", "l.count"),
    PRICE("price", "l.price"),
    ;
    private final String code;
    private final String column;

    public static QuantityLogsOrderByType fromCode(String code) {
        return Arrays.stream(QuantityLogsOrderByType.values())
                .filter(type -> type.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}
