package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ItemRS {
    private Long itemNo;
    private String name;
    private String type;
    private String room;
    private String place;
    private String locationMemo;
    private String photoUrl;
    private int quantity;
    private int priority;
    List<LabelRS> labels;
}
