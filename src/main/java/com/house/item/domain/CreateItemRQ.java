package com.house.item.domain;

import com.house.item.entity.ItemType;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

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
    private MultipartFile photo;
    @Range(min = 0, max = 5)
    private Integer priority;

    public CreateItemRQ(String name, ItemType type, Long locationNo, String locationMemo, MultipartFile photo, Integer priority) {
        this.name = name;
        this.type = type;
        this.locationNo = locationNo;
        this.locationMemo = locationMemo;
        this.photo = photo;
        if (priority == null) {
            this.priority = 0;
        } else {
            this.priority = priority;
        }
    }
}
