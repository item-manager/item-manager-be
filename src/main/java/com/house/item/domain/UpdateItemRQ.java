package com.house.item.domain;

import com.house.item.entity.ItemType;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class UpdateItemRQ {
    @NotBlank
    private String name;
    @NotNull
    private ItemType type;
    @NotNull
    private Long locationNo;
    private String locationMemo;
    private String photoName;
    @Builder.Default
    @Range(min = 0, max = 5)
    private Integer priority = 0;
    private List<Long> labels;
}
