package com.house.item.domain;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UpdateRoomRQ {
    @NotBlank
    private String name;
}
