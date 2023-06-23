package com.house.item.util;

import java.util.Optional;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.SessionUser;
import com.house.item.exception.NonExistentSessionUserException;
import com.house.item.web.SessionConst;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionUtils {

    public static void setAttribute(String name, Object value) throws IllegalStateException {
        RequestContextHolder.currentRequestAttributes().setAttribute(name, value, RequestAttributes.SCOPE_SESSION);
    }

    public static Object getAttribute(String name) throws IllegalStateException {
        return RequestContextHolder.currentRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    public static void removeAttribute(String name) throws IllegalStateException {
        RequestContextHolder.currentRequestAttributes().removeAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    public static SessionUser getSessionUser() {
        return Optional.ofNullable(SessionUtils.getAttribute(SessionConst.LOGIN_USER))
            .map(SessionUser.class::cast)
            .orElseThrow(
                () -> new NonExistentSessionUserException(ExceptionCodeMessage.NON_EXISTENT_SESSION_USER.message()));
    }
}
