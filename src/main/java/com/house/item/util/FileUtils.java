package com.house.item.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import com.house.item.exception.ServiceException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static String getFullPath(String dir, String fileName) {
        return dir + fileName;
    }

    public static String storeFile(MultipartFile multipartFile, String fileDir) throws ServiceException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFileName = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFileName);
        //서버에 파일 저장
        try {
            multipartFile.transferTo(new File(fileDir + storeFileName));
        } catch (IOException e) {
            log.error("파일 저장 중 IOException 발생: e = ", e);
            throw new ServiceException("파일 저장 중 IOException 발생", e);
        }

        return storeFileName;
    }

    public static Resource getResource(String dir, String filename) throws ServiceException {
        Resource resource;
        String url = getFullPath(dir, filename);
        try {
            resource = new UrlResource("file:" + url);
        } catch (MalformedURLException e) {
            log.error("유효하지 않은 URL입니다: {}", url, e);
            throw new ServiceException("유효하지 않은 URL입니다: " + url, e);
        }
        return resource;
    }

    public static void deleteFile(String dir, String filename) throws ServiceException {
        String fullPath = getFullPath(dir, filename);

        try {
            Files.delete(Path.of(fullPath));
        } catch (NoSuchFileException e) {
            log.error("존재하지 않는 파일 제거 시도: e = ", e);
        } catch (IOException e) {
            log.error("파일 제거 중 IOException 발생: e = ", e);
        } catch (SecurityException e) {
            log.error("파일 제거 권한이 없습니다: e = ", e);
        }
    }

    //서버 내부에서 관리하는 별도의 파일명(실제 저장될 파일명)
    private static String createStoreFileName(String originalFileName) {
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    //서버내부관리 파일명에 확장자를 붙히기위해 원래 파일명에서 확장자 추출
    private static String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf('.');
        return originalFileName.substring(pos + 1);
    }
}
