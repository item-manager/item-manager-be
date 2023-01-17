package com.house.item.service;

import com.house.item.domain.LoginUserRQ;
import com.house.item.domain.SessionUser;
import com.house.item.entity.User;
import com.house.item.exception.IncorrectUserIdPasswordException;
import com.house.item.exception.ServiceException;
import com.house.item.repository.UserRepository;
import com.house.item.util.Encrypt;
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
                .orElseThrow(() -> new IncorrectUserIdPasswordException("아이디와 패스워드가 일치하지 않습니다"));

        if (!Encrypt.isRightPassword(findUser.getPassword(), findUser.getSalt(), loginUserRQ.getPassword())) {
            throw new IncorrectUserIdPasswordException("아이디와 패스워드가 일치하지 않습니다");
        }

        return SessionUser.builder()
                .userNo(findUser.getUserNo())
                .id(findUser.getId())
                .build();
    }
}
