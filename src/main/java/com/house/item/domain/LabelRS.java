package com.house.item.domain;

import com.house.item.entity.Label;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LabelRS {
    private Long labelNo;
    private String name;

    public static LabelRS of(Label label) {
        return LabelRS.builder()
            .labelNo(label.getLabelNo())
            .name(label.getName())
            .build();
    }
}
