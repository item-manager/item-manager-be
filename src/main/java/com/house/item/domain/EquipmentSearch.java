package com.house.item.domain;

import com.house.item.entity.Label;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EquipmentSearch {
    private Long userNo;
    private String name;
    private List<Label> labels;
    private List<Long> placeNos;
    private int page;
    private int size;
}
