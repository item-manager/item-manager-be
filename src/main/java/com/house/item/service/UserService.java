package com.house.item.service;

import com.house.item.domain.CreateUserRQ;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentUserException;
import com.house.item.exception.NonUniqueUserIdException;
import com.house.item.repository.UserRepository;
import com.house.item.util.Encrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long signUp(CreateUserRQ createUserRQ) throws ServiceException, NonUniqueUserIdException {
        validateDuplicationUser(createUserRQ.getId());

        //review - test 필요
        String salt = Encrypt.getSalt();
        User user = User.builder()
                .id(createUserRQ.getId())
                .password(Encrypt.getEncrypt(createUserRQ.getPassword(), salt))
                .salt(salt)
                .build();

        return userRepository.save(user);
    }

    //review - test 필요
    private void validateDuplicationUser(String id) throws NonUniqueUserIdException {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(u -> {
            throw new NonUniqueUserIdException("이미 존재하는 아이디입니다.");
        });
    }

    @Transactional
    public void removeUser(Long userNo) throws NonExistentUserException {
        Optional<User> optionalUser = userRepository.findOne(userNo);
        User user = optionalUser.orElseThrow(() -> new NonExistentUserException("존재하지 않는 회원입니다"));
        userRepository.delete(user);
    }
}
