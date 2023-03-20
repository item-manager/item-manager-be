package com.house.item.web;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.common.Props;
import com.house.item.domain.Result;
import com.house.item.domain.SaveImageRS;
import com.house.item.exception.NotContentTypeImageException;
import com.house.item.exception.ServiceException;
import com.house.item.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final Props props;

    @Operation(summary = "이미지 저장")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<SaveImageRS> saveImage(@RequestParam MultipartFile file) {
        if (file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("image")) {
            throw new NotContentTypeImageException(ExceptionCodeMessage.NOT_CONTENT_TYPE_IMAGE_EXCEPTION.message());
        }
        String dir = props.getDir().getFile();
        String filename = FileUtil.storeFile(file, dir);

        SaveImageRS imageRS = SaveImageRS.builder()
                .filename(filename)
                .build();
        return Result.<SaveImageRS>builder()
                .data(imageRS)
                .build();
    }

    @Operation(summary = "이미지 조회")
    @GetMapping("/{filename}")
    public Resource loadImage(@PathVariable String filename) throws ServiceException {
        String dir = props.getDir().getFile();
        return FileUtil.getResource(dir, filename);
    }
}
