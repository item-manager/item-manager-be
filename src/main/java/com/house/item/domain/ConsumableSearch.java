package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ConsumableSearch {
    private Long userNo;
    private String name;
    private List<Long> labelNos;
    private String orderBy;
    private String sort;
    private int page;
    private int size;
}
