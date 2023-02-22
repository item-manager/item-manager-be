package com.house.item.util;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

@Slf4j
class FileUtilTest {

    String dir = "/Users/yurim/home/dev/webDev/projects/item-manager/stored-file/test/";

    @Test
    void deleteFile() throws Exception {
        //given
        MultipartFile photo = new MockMultipartFile(
                "fileName",
                "/Users/yurim/Downloads/KakaoTalk_Photo_2023-01-27-16-28-12-3.jpeg",
                "image/jpeg",
                new FileInputStream("/Users/yurim/Downloads/KakaoTalk_Photo_2023-01-27-16-28-12-3.jpeg"));
        String storeFileName = FileUtil.storeFile(photo, dir);
        log.debug("deleteFile: storeFileName = {}", storeFileName);

        //when
        FileUtil.deleteFile(dir, storeFileName);

        //then
        String fullPath = FileUtil.getFullPath(dir, storeFileName);
        UrlResource urlResource = new UrlResource("file:" + fullPath);
        File file = urlResource.getFile();
        Assertions.assertThat(file).doesNotExist();
    }
}