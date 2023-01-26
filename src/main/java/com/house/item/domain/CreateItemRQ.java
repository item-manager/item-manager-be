package com.house.item.domain;

import com.house.item.entity.ItemType;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CreateItemRQ {
    @NotBlank
    private String name;
    @NotBlank
    private ItemType type;
    @NotBlank
    private Long locationNo;
    private String locationMemo;
//    private MultipartFile photo;

    private int priority = 0;
}
