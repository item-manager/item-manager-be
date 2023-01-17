package com.house.item.util;

import com.house.item.exception.ServiceException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncryptUtils {
    public static String getSalt() {
        //Random, byte 객체 생성
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[20];

        //난수 생성
        sr.nextBytes(salt);

        //byte인 salt를 16진수 string으로 변환
        StringBuilder sb = new StringBuilder();
        for (byte s : salt) {
            sb.append(String.format("%02x", s));
        }
        return sb.toString();
    }

    public static String getEncrypt(String password, String salt) throws ServiceException {
        try {
            //SHA256 알고리즘 객체
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            //password + salt에 SHA256 적용
            md.update((password + salt).getBytes());
            byte[] shaPassword = md.digest();

            //SHA256이 적용된 byte를 16진수 string으로 변환
            StringBuilder sb = new StringBuilder();
            for (byte p : shaPassword) {
                sb.append(String.format("%02x", p));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("사용할 수 없는 암호화 알고리즘: e = ", e);
            throw new ServiceException("사용할 수 없는 암호화 알고리즘", e);
        }
    }

    public static boolean isRightPassword(String encrypted, String salt, String inputPassword) {
        return encrypted.equals(EncryptUtils.getEncrypt(inputPassword, salt));
    }
}
