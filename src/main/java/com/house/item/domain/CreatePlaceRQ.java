package com.house.item.domain;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CreatePlaceRQ {

    @NotBlank
    private Long roomNo;
    @NotBlank
    private String name;
}
