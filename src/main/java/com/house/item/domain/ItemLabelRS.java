package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemLabelRS {
    private Long itemNo;
    private Long labelNo;
}
