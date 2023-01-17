package com.house.item.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionCodeMessage {
    NON_EXISTENT_USER(1001, "존재하지 않는 회원입니다"),
    NON_UNIQUE_USER_ID(1002, "이미 존재하는 회원 아이디입니다"),
    INCORRECT_USER_ID_PASSWORD(1003, "아이디와 패스워드가 일치하지 않습니다");

    private final int code;
    private final String message;

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
