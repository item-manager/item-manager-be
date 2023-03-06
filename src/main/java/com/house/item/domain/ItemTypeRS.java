package com.house.item.domain;

import com.house.item.entity.ItemType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ItemTypeRS {
    CONSUMABLE(ItemType.CONSUMABLE, "소모품"),
    EQUIPMENT(ItemType.EQUIPMENT, "비품"),
    ;
    private final ItemType type;
    private final String name;

    public static ItemTypeRS fromType(ItemType type) {
        return Arrays.stream(ItemTypeRS.values())
                .filter(rs -> rs.type == type)
                .findFirst()
                .orElse(null);
    }
}
