package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.CreateUserRQ;
import com.house.item.entity.User;
import com.house.item.exception.NonExistentUserException;
import com.house.item.exception.NonUniqueUserIdException;
import com.house.item.exception.NonUniqueUsernameException;
import com.house.item.repository.UserRepository;
import com.house.item.util.EncryptUtils;
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
    public Long signUp(CreateUserRQ createUserRQ) throws ServiceException, NonUniqueUserIdException, NonUniqueUsernameException {
        validateDuplicationUserId(createUserRQ.getId());
        validateDuplicationUsername(createUserRQ.getUsername());

        //review - test 필요
        String salt = EncryptUtils.getSalt();
        User user = User.builder()
                .id(createUserRQ.getId())
                .password(EncryptUtils.getEncrypt(createUserRQ.getPassword(), salt))
                .salt(salt)
                .username(createUserRQ.getUsername())
                .build();

        userRepository.save(user);
        return user.getUserNo();
    }

    //review - test 필요
    private void validateDuplicationUserId(String id) throws NonUniqueUserIdException {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(u -> {
            throw new NonUniqueUserIdException(ExceptionCodeMessage.NON_UNIQUE_USER_ID.message());
        });
    }

    private void validateDuplicationUsername(String username) throws NonUniqueUsernameException {
        Optional<User> user = userRepository.findByUsername(username);
        user.ifPresent(u -> {
            throw new NonUniqueUsernameException(ExceptionCodeMessage.NON_UNIQUE_USERNAME.message());
        });
    }

    @Transactional
    public void removeUser(Long userNo) throws NonExistentUserException {
        Optional<User> optionalUser = userRepository.findOne(userNo);
        User user = optionalUser.orElseThrow(() -> new NonExistentUserException(ExceptionCodeMessage.NON_EXISTENT_USER.message()));
        userRepository.delete(user);
    }
}
