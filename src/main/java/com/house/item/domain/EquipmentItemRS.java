package com.house.item.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EquipmentItemRS {
    private Long itemNo;
    private int priority;
    private String name;
    private String roomName;
    private String placeName;
    private List<LabelRS> labels;
}
