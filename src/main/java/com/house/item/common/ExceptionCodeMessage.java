package com.house.item.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionCodeMessage {
    NON_EXISTENT_USER(CodeDefine.NON_EXISTENT_USER, MessageDefine.NON_EXISTENT_USER),
    NON_SESSION_USER(CodeDefine.NON_SESSION_USER, MessageDefine.NON_SESSION_USER),
    INCORRECT_USER_ID_PASSWORD(CodeDefine.INCORRECT_USER_ID_PASSWORD, MessageDefine.INCORRECT_USER_ID_PASSWORD),
    NON_UNIQUE_USER_ID(CodeDefine.NON_UNIQUE_USER_ID, MessageDefine.NON_UNIQUE_USER_ID),
    NON_UNIQUE_USERNAME(CodeDefine.NON_UNIQUE_USERNAME, MessageDefine.NON_UNIQUE_USERNAME),

    ;

    public interface CodeDefine {
        int NON_EXISTENT_USER = 1001;
        int NON_SESSION_USER = 1002;
        int INCORRECT_USER_ID_PASSWORD = 1003;
        int NON_UNIQUE_USER_ID = 1004;
        int NON_UNIQUE_USERNAME = 1005;
    }

    public interface MessageDefine {
        String NON_EXISTENT_USER = "존재하지 않는 회원입니다";
        String NON_SESSION_USER = "로그인 세션이 존재하지 않습니다";
        String INCORRECT_USER_ID_PASSWORD = "아이디와 패스워드가 일치하지 않습니다";
        String NON_UNIQUE_USER_ID = "이미 존재하는 회원 아이디입니다";
        String NON_UNIQUE_USERNAME = "이미 존재하는 회원 이름입니다";
    }

    public interface SwaggerDescription {
        String NON_EXISTENT_USER = CodeDefine.NON_EXISTENT_USER + " - " + MessageDefine.NON_EXISTENT_USER;
        String INCORRECT_USER_ID_PASSWORD = CodeDefine.INCORRECT_USER_ID_PASSWORD + " - " + MessageDefine.INCORRECT_USER_ID_PASSWORD;
        String NON_UNIQUE_USER_ID = CodeDefine.NON_UNIQUE_USER_ID + " - " + MessageDefine.NON_UNIQUE_USER_ID;
        String NON_UNIQUE_USERNAME = CodeDefine.NON_UNIQUE_USERNAME + " - " + MessageDefine.NON_UNIQUE_USERNAME;
    }

    private final int code;
    private final String message;

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
