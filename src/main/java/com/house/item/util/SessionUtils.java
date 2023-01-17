package com.house.item.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

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

}
