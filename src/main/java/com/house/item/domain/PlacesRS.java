package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlacesRS {
    private Long placeNo;
    private String name;
}
