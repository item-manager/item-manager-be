package com.house.item.service;

import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.common.Props;
import com.house.item.domain.ChangePasswordRQ;
import com.house.item.domain.CreateUserRQ;
import com.house.item.domain.UpdateUserInfoRQ;
import com.house.item.domain.UserRS;
import com.house.item.entity.User;
import com.house.item.exception.IncorrectUserIdPasswordException;
import com.house.item.exception.NonExistentUserException;
import com.house.item.exception.NonUniqueUserIdException;
import com.house.item.exception.NonUniqueUsernameException;
import com.house.item.repository.UserRepository;
import com.house.item.util.EncryptUtils;
import com.house.item.util.FileUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final Props props;
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

    public UserRS userToUserRS(User user) {
        UserRS.UserRSBuilder userRSBuilder = UserRS.builder()
                .userNo(user.getUserNo())
                .username(user.getUsername());

        if (StringUtils.hasText(user.getPhotoName())) {
            userRSBuilder.photoUrl("/images/" + user.getPhotoName());
        }

        return userRSBuilder.build();
    }

    @Transactional
    public void updateUserInfo(User loginUser, UpdateUserInfoRQ updateUserInfoRQ) {
        String photoDir = props.getDir().getFile();
        if (StringUtils.hasText(loginUser.getPhotoName()) && !loginUser.getPhotoName().equals(updateUserInfoRQ.getPhotoName())) {
            FileUtils.deleteFile(photoDir, loginUser.getPhotoName());
        }

        loginUser.updateUserInfo(updateUserInfoRQ.getUsername(), updateUserInfoRQ.getPhotoName());
    }

    @Transactional
    public void changePassword(User loginUser, ChangePasswordRQ changePasswordRQ) throws NonExistentUserException, IncorrectUserIdPasswordException {
        checkPassword(loginUser, changePasswordRQ.getCurrentPassword());

        String newSalt = EncryptUtils.getSalt();

        loginUser.changePasswordAndSalt(EncryptUtils.getEncrypt(changePasswordRQ.getNewPassword(), newSalt), newSalt);
    }

    private void checkPassword(User loginUser, String inputPassword) {
        if (!EncryptUtils.isRightPassword(loginUser.getPassword(), loginUser.getSalt(), inputPassword)) {
            throw new IncorrectUserIdPasswordException(ExceptionCodeMessage.INCORRECT_USER_ID_PASSWORD.message());
        }
    }

    @Transactional
    public void removeUser(Long userNo) throws NonExistentUserException {
        Optional<User> optionalUser = userRepository.findOne(userNo);
        User user = optionalUser.orElseThrow(() -> new NonExistentUserException(ExceptionCodeMessage.NON_EXISTENT_USER.message()));
        userRepository.delete(user);
    }
}
