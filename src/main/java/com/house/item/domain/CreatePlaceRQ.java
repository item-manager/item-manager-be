package com.house.item.domain;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class CreatePlaceRQ {

    @NotNull
    private Long roomNo;
    @NotBlank
    private String name;
}
