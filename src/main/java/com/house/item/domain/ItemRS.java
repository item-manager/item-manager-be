package com.house.item.domain;

import com.house.item.entity.ItemType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemRS {
    private Long itemNo;
    private String name;
    private ItemType type;
    private String room;
    private String place;
    private String locationMemo;
    //    private String photoUrl;
    private int quantity;
    private int priority;
}
