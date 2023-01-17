package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.LoginUserRQ;
import com.house.item.domain.SessionUser;
import com.house.item.entity.User;
import com.house.item.exception.IncorrectUserIdPasswordException;
import com.house.item.exception.ServiceException;
import com.house.item.repository.UserRepository;
import com.house.item.util.EncryptUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
                .id(findUser.getId())
                .build();
    }
}
