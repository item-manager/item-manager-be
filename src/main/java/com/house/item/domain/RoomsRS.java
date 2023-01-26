package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomsRS {
    private Long roomNo;
    private String name;
}
