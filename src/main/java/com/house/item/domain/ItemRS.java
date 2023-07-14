package com.house.item.domain;

import java.util.List;

import com.house.item.entity.ItemType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemRS {
    private Long itemNo;
    private String name;
    private ItemType type;
    private Long roomNo;
    private Long placeNo;
    private String room;
    private String place;
    private String photoUrl;
    private int quantity;
    private int priority;
    private String memo;
    List<LabelRS> labels;
}
