package com.house.item.domain;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CreateRoomRQ {
    @NotBlank
    private String name;
}
