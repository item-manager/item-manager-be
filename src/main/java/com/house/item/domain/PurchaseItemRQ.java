package com.house.item.domain;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class PurchaseItemRQ {
    private String mall;
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "UTC")
    private LocalDateTime date;
    @Min(0)
    private int price;
    @Min(0)
    private int count;
}
