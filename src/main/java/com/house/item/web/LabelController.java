package com.house.item.web;

import com.house.item.domain.CreateLabel;
import com.house.item.domain.CreateLabelRQ;
import com.house.item.domain.CreateLabelRS;
import com.house.item.domain.Result;
import com.house.item.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/labels")
public class LabelController {
    private final LabelService labelService;

    @Operation(summary = "라벨 생성")
    @PostMapping
    public Result<CreateLabelRS> createLabel(@RequestBody CreateLabelRQ createLabelRQ) {
        CreateLabel createLabel = CreateLabel.builder()
                .name(createLabelRQ.getName())
                .build();
        Long labelNo = labelService.createLabel(createLabel);

        CreateLabelRS createLabelRS = CreateLabelRS.builder()
                .labelNo(labelNo)
                .build();
        return Result.<CreateLabelRS>builder()
                .data(createLabelRS)
                .build();
    }
}
