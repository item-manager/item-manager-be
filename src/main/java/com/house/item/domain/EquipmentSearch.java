package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EquipmentSearch {
    private Long userNo;
    private String name;
    private List<Long> labelNos;
    private List<Long> placeNos;
    private int page;
    private int size;
}
