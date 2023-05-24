package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRS {
    private Long userNo;
    private String username;
    private String photoUrl;
}
