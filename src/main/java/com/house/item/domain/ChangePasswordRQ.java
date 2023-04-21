package com.house.item.domain;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
public class ChangePasswordRQ {
    @NotBlank
    private String currentPassword;
    @NotBlank
    @Pattern(regexp = "^(?=\\w*\\d)(?=\\w*[a-z])(?=\\w*[A-Z])[a-zA-Z\\\\d`~!@#$%^&*()-_=+]{6,20}$")
    private String newPassword;
}
