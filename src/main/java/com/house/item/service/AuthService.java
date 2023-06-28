package com.house.item.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.LoginUserRQ;
import com.house.item.domain.SessionUser;
import com.house.item.entity.User;
import com.house.item.exception.IncorrectUserIdPasswordException;
import com.house.item.exception.NonExistentSessionUserException;
import com.house.item.exception.NonExistentUserException;
import com.house.item.exception.ServiceException;
import com.house.item.repository.UserRepository;
import com.house.item.util.EncryptUtils;
import com.house.item.util.SessionUtils;
import com.house.item.web.SessionConst;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;

    public SessionUser login(LoginUserRQ loginUserRQ) throws IncorrectUserIdPasswordException, ServiceException {
        Optional<User> findUserOptional = userRepository.findById(loginUserRQ.getId());

        User findUser = findUserOptional
                .orElseThrow(() -> new IncorrectUserIdPasswordException(ExceptionCodeMessage.INCORRECT_USER_ID_PASSWORD.message()));

        if (!EncryptUtils.isRightPassword(findUser.getPassword(), findUser.getSalt(), loginUserRQ.getPassword())) {
            throw new IncorrectUserIdPasswordException(ExceptionCodeMessage.INCORRECT_USER_ID_PASSWORD.message());
        }

        return SessionUser.builder()
                .userNo(findUser.getUserNo())
                .username(findUser.getUsername())
                .build();
    }

    public User getLoginUser() throws NonExistentSessionUserException, NonExistentUserException {
        SessionUser sessionUser = (SessionUser)SessionUtils.getAttribute(SessionConst.LOGIN_USER);
        if (sessionUser == null) {
            throw new NonExistentSessionUserException(ExceptionCodeMessage.NON_EXISTENT_SESSION_USER.message());
        }
        return userRepository.findById(sessionUser.getUserNo())
            .orElseThrow(() -> new NonExistentUserException(ExceptionCodeMessage.NON_EXISTENT_USER.message()));
    }
}
