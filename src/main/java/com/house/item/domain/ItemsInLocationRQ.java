package com.house.item.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class ItemsInLocationRQ {
    @NotNull
    private Long locationNo;
}
