package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DetachLabelFromItemRQ {
    private Long itemNo;
    private Long labelNo;
}
