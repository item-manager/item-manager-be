package com.house.item.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ExceptionCodeMessage {

    /* User */
    NON_EXISTENT_USER(CodeDefine.NON_EXISTENT_USER, MessageDefine.NON_EXISTENT_USER),
    NON_EXISTENT_SESSION_USER(CodeDefine.NON_EXISTENT_SESSION_USER, MessageDefine.NON_EXISTENT_SESSION_USER),
    INCORRECT_USER_ID_PASSWORD(CodeDefine.INCORRECT_USER_ID_PASSWORD, MessageDefine.INCORRECT_USER_ID_PASSWORD),
    NON_UNIQUE_USER_ID(CodeDefine.NON_UNIQUE_USER_ID, MessageDefine.NON_UNIQUE_USER_ID),
    NON_UNIQUE_USERNAME(CodeDefine.NON_UNIQUE_USERNAME, MessageDefine.NON_UNIQUE_USERNAME),

    /* Item */
    NON_EXISTENT_ITEM(CodeDefine.NON_EXISTENT_ITEM, MessageDefine.NON_EXISTENT_ITEM),

    /* Location */
    NON_EXISTENT_ROOM(CodeDefine.NON_EXISTENT_ROOM, MessageDefine.NON_EXISTENT_ROOM),
    NON_EXISTENT_PLACE(CodeDefine.NON_EXISTENT_PLACE, MessageDefine.NON_EXISTENT_PLACE),
    NOT_LOCATION_TYPE_ROOM(CodeDefine.NOT_LOCATION_TYPE_ROOM, MessageDefine.NOT_LOCATION_TYPE_ROOM),
    NON_EXISTENT_LOCATION(CodeDefine.NON_EXISTENT_LOCATION, MessageDefine.NON_EXISTENT_LOCATION),

    /* Label */
    NON_EXISTENT_LABEL(CodeDefine.NON_EXISTENT_LABEL, MessageDefine.NON_EXISTENT_LABEL),
    NON_UNIQUE_LABEL_NAME(CodeDefine.NON_UNIQUE_LABEL_NAME, MessageDefine.NON_UNIQUE_LABEL_NAME),

    ;

    public interface CodeDefine {

        /* User */
        int NON_EXISTENT_USER = 1001;
        int NON_EXISTENT_SESSION_USER = 1002;
        int INCORRECT_USER_ID_PASSWORD = 1003;
        int NON_UNIQUE_USER_ID = 1004;
        int NON_UNIQUE_USERNAME = 1005;

        /* Item */
        int NON_EXISTENT_ITEM = 2001;

        /* Location */
        int NON_EXISTENT_ROOM = 3001;
        int NON_EXISTENT_PLACE = 3002;
        int NOT_LOCATION_TYPE_ROOM = 3003;
        int NON_EXISTENT_LOCATION = 3004;

        /* Label */
        int NON_EXISTENT_LABEL = 4001;
        int NON_UNIQUE_LABEL_NAME = 4002;
    }

    public interface MessageDefine {

        /* User */
        String NON_EXISTENT_USER = "존재하지 않는 회원입니다";
        String NON_EXISTENT_SESSION_USER = "로그인 세션이 존재하지 않습니다";
        String INCORRECT_USER_ID_PASSWORD = "아이디와 패스워드가 일치하지 않습니다";
        String NON_UNIQUE_USER_ID = "이미 존재하는 회원 아이디입니다";
        String NON_UNIQUE_USERNAME = "이미 존재하는 회원 이름입니다";

        /* Item */
        String NON_EXISTENT_ITEM = "잘못된 물품 정보입니다";

        /* Location */
        String NON_EXISTENT_ROOM = "잘못된 방 정보입니다";
        String NON_EXISTENT_PLACE = "잘못된 위치 정보입니다";
        String NOT_LOCATION_TYPE_ROOM = "roomNo type이 방이 아닙니다";
        String NON_EXISTENT_LOCATION = "잘못된 방/위치 정보입니다";

        /* Label */
        String NON_EXISTENT_LABEL = "잘못된 라벨 정보입니다";
        String NON_UNIQUE_LABEL_NAME = "이미 존재하는 라벨 이름입니다";
    }

    public interface SwaggerDescription {

        /* User */
        String NON_EXISTENT_USER = CodeDefine.NON_EXISTENT_USER + " - " + MessageDefine.NON_EXISTENT_USER;
        String INCORRECT_USER_ID_PASSWORD = CodeDefine.INCORRECT_USER_ID_PASSWORD + " - " + MessageDefine.INCORRECT_USER_ID_PASSWORD;
        String NON_UNIQUE_USER_ID = CodeDefine.NON_UNIQUE_USER_ID + " - " + MessageDefine.NON_UNIQUE_USER_ID;
        String NON_UNIQUE_USERNAME = CodeDefine.NON_UNIQUE_USERNAME + " - " + MessageDefine.NON_UNIQUE_USERNAME;

        /* Item */
        String NON_EXISTENT_ITEM = CodeDefine.NON_EXISTENT_ITEM + " - " + MessageDefine.NON_EXISTENT_ITEM;

        /* Location */
        String NON_EXISTENT_ROOM = CodeDefine.NON_EXISTENT_ROOM + " - " + MessageDefine.NON_EXISTENT_ROOM;
        String NON_EXISTENT_PLACE = CodeDefine.NON_EXISTENT_PLACE + " - " + MessageDefine.NON_EXISTENT_PLACE;
        String NOT_LOCATION_TYPE_ROOM = CodeDefine.NOT_LOCATION_TYPE_ROOM + " - " + MessageDefine.NOT_LOCATION_TYPE_ROOM;
        String NON_EXISTENT_LOCATION = CodeDefine.NON_EXISTENT_LOCATION + " - " + MessageDefine.NON_EXISTENT_LOCATION;

        /* Label */
        String NON_EXISTENT_LABEL = CodeDefine.NON_EXISTENT_LABEL + " - " + MessageDefine.NON_EXISTENT_LABEL;
        String NON_UNIQUE_LABEL_NAME = CodeDefine.NON_UNIQUE_LABEL_NAME + " - " + MessageDefine.NON_UNIQUE_LABEL_NAME;
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
