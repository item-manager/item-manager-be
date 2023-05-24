package com.house.item.domain;

import com.house.item.entity.QuantityType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuantityTypeRQ {
    purchase(QuantityType.PURCHASE),
    consume(QuantityType.CONSUME),
    ;
    private final QuantityType type;
}
