package com.house.item.domain;

import com.house.item.entity.ItemType;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CreateItemRQ {
    @NotBlank
    private String name;
    @NotNull
    private ItemType type;
    @NotNull
    private Long locationNo;
    private String locationMemo;
    private String photoName;
    @Range(min = 0, max = 6)
    private Integer priority = 0;
    private String memo;
    private List<Long> labels = new ArrayList<>();
}
