package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateLabelRQ {
    private String name;
}
