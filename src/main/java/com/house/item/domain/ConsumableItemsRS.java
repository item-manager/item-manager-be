package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ConsumableItemsRS {
    private Page page;
    private List<ConsumableItemRS> consumableItems;
}
