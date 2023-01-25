package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginUserRS {
    private Long userNo;

    private String username;
}
