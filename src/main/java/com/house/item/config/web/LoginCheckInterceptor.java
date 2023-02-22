package com.house.item.config.web;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.exception.NonExistentSessionUserException;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (SessionUtils.getAttribute(SessionConst.LOGIN_USER) == null) {
            throw new NonExistentSessionUserException(ExceptionCodeMessage.NON_EXISTENT_SESSION_USER.message());
        }
        return true;
    }
}
