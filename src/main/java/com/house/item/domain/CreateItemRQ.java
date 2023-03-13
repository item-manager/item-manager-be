package com.house.item.domain;

import com.house.item.entity.ItemType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateItemRQ {
    @NotBlank
    private String name;
    @NotNull
    private ItemType type;
    @NotNull
    private Long locationNo;
    private String locationMemo;
    private MultipartFile photo;
    private String photoName;
    @Range(min = 0, max = 5)
    private Integer priority = 0;
    private List<Long> labels = new ArrayList<>();

    public CreateItemRQ(String name, ItemType type, Long locationNo, String locationMemo, MultipartFile photo, Integer priority, List<Long> labels) {
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
        if (labels == null) {
            this.labels = new ArrayList<>();
        } else {
            this.labels = labels;
        }
    }
}
