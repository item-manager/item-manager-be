package com.house.item.domain;

import com.house.item.entity.QuantityType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum QuantityTypeRS {
    PURCHASE(QuantityType.PURCHASE, "구매"),
    CONSUME(QuantityType.CONSUME, "사용"),
    ;
    private final QuantityType type;
    private final String name;

    public static QuantityTypeRS fromType(QuantityType type) {
        return Arrays.stream(QuantityTypeRS.values())
                .filter(rs -> rs.type == type)
                .findFirst()
                .orElse(null);
    }
}
