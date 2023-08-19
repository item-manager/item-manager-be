package com.house.item.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsumableSearch {
    private Long userNo;
    private String name;
    private List<Long> labelNos;
    private boolean checkThreshold;
    private Pageable pageable;
}
