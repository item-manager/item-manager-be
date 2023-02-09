package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class UpdateRoomRQ {
    @NotBlank
    private String name;
}
