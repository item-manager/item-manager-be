package com.house.item.web;

import com.house.item.common.Props;
import com.house.item.exception.ServiceException;
import com.house.item.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CommonController {

    private final Props props;

    @Operation(summary = "사진 조회")
    @GetMapping("/photo/{filename}")
    public Resource loadPhoto(@PathVariable String filename) throws ServiceException {
        String photoDir = props.getDir().getPhoto();
        return FileUtil.getResource(photoDir, filename);
    }
}
