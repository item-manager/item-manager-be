package com.house.item.domain;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ChangePasswordRQ {
    @NotBlank
    private String currentPassword;
    @NotBlank
    private String newPassword;
}
