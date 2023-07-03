package com.house.item.domain;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import com.house.item.entity.ItemType;

import lombok.Getter;

@Getter
public class CreateItemRQ2 {
    @NotBlank
    private String name;
    @NotNull
    private ItemType type;
    @NotNull
    private Long locationNo;
    private String photoName;
    @Range(min = 0, max = 6)
    private Integer priority = 0;
    private String memo;
    private List<Long> labels = new ArrayList<>();
}
