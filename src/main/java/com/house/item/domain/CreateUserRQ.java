package com.house.item.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor //review - test에서 reflection 방식으로 입력
@NoArgsConstructor
public class CreateUserRQ {

    @NotBlank
    private String id;

    @NotBlank
    private String password;
}
