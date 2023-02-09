package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class UpdatePlaceRQ {
    @NonNull
    private Long roomNo;
    @NotBlank
    private String name;
}
