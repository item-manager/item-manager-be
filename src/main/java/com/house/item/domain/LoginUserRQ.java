package com.house.item.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserRQ {

    @NotBlank
    private String id;

    @NotBlank
    private String password;
}
