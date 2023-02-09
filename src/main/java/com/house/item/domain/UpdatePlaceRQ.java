package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
public class UpdatePlaceRQ {
    @NotNull
    private Long roomNo;
    @NotBlank
    private String name;
}
