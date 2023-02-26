package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemNameRS {
    private Long itemNo;
    private String name;
}
