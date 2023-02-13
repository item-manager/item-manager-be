package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Getter
@Builder
public class ConsumeItemRQ {
    private LocalDateTime date;
    @Min(0)
    private int count;
}
