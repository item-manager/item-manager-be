package com.house.item.web;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.*;
import com.house.item.entity.Label;
import com.house.item.exception.NonUniqueLabelNameException;
import com.house.item.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/labels")
public class LabelController {
    private final LabelService labelService;

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_UNIQUE_LABEL_NAME)
                    }
            )
    )
    @Operation(summary = "라벨 생성")
    @PostMapping
    public Result<CreateLabelRS> createLabel(@RequestBody CreateLabelRQ createLabelRQ) throws NonUniqueLabelNameException {
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

    @Operation(summary = "라벨 목록 조회")
    @GetMapping
    public Result<List<LabelRS>> getLabels() {
        List<Label> labels = labelService.getLabels();

        List<LabelRS> labelRSList = labelService.labelToLabelRS(labels);
        return Result.<List<LabelRS>>builder()
                .data(labelRSList)
                .build();
    }

    @ApiResponse(
            responseCode = "400",
            content = @Content(
                    schema = @Schema(implementation = ErrorResult.class),
                    examples = {
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_LABEL)
                    }
            )
    )
    @Operation(summary = "라벨 제거")
    @DeleteMapping("/{labelNo}")
    public Result<Void> deleteLabel(@PathVariable Long labelNo) {
        labelService.deleteLabel(labelNo);

        return Result.<Void>builder()
                .code(200)
                .message("ok")
                .build();
    }
}
