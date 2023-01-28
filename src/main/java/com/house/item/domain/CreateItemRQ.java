package com.house.item.domain;

import com.house.item.entity.ItemType;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class CreateItemRQ {
    @NotBlank
    private String name;
    @NotNull
    private ItemType type;
    @NotNull
    private Long locationNo;
    private String locationMemo;
    //    private MultipartFile photo;
    @Range(min = 0, max = 5)
    private Integer priority = 0;
}
