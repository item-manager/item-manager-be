package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResult {
    private int code;
    private String message;
}
