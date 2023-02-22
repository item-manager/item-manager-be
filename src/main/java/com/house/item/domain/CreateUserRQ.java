package com.house.item.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor //review - test에서 reflection 방식으로 입력
@NoArgsConstructor
public class CreateUserRQ {

    @Pattern(regexp = "^\\w{2,10}$")
    private String id;

    @Pattern(regexp = "^(?=\\w*\\d)(?=\\w*[a-z])(?=\\w*[A-Z])\\w{6,20}$")
    private String password;

    @Pattern(regexp = "^[ㄱ-ㅎ가-힣\\w]{2,10}$")
    private String username;
}
