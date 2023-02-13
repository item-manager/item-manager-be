package com.house.item.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Getter
public class ConsumeItemRQ {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime date;
    @Min(0)
    private int count;
}
