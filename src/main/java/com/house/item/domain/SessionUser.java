package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class SessionUser implements Serializable {
    private Long userNo;
    private String username;
}
