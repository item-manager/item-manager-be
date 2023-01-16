package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Result<T> {
    private int code;
    private String message;
    private T data;
}
