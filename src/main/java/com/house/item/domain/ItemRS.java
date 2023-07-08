package com.house.item.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemRS {
    private Long itemNo;
    private String name;
    private String type;
    private Long locationNo;
    private String room;
    private String place;
    private String photoUrl;
    private int quantity;
    private int priority;
    private String memo;
    List<LabelRS> labels;
}
