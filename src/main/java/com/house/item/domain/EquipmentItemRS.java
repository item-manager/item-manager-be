package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class EquipmentItemRS {
    private Long itemNo;
    private int priority;
    private String name;
    private String roomName;
    private String placeName;
    private String locationMemo;
    private List<LabelRS> labels;
}
