package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttachLabelToItemRQ {
    private Long labelNo;
}
