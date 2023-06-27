package com.house.item.web;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.CreateLabel;
import com.house.item.domain.CreateLabelRQ;
import com.house.item.domain.CreateLabelRS;
import com.house.item.domain.ErrorResult;
import com.house.item.domain.LabelRS;
import com.house.item.domain.Result;
import com.house.item.domain.UpdateLabelRQ;
import com.house.item.entity.Label;
import com.house.item.entity.User;
import com.house.item.exception.NonUniqueLabelNameException;
import com.house.item.service.LabelService;
import com.house.item.util.SessionUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        User user = SessionUtils.getSessionUser().toUser();

        CreateLabel createLabel = CreateLabel.builder()
            .name(createLabelRQ.getName())
            .build();
        Long labelNo = labelService.createLabel(createLabel, user);

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
        User user = SessionUtils.getSessionUser().toUser();

        List<Label> labels = labelService.getLabels(user);

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
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_LABEL),
                            @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_UNIQUE_LABEL_NAME)
                    }
            )
    )
    @Operation(summary = "라벨 수정")
    @PatchMapping("/{labelNo}")
    public Result<Void> patchLabel(@PathVariable Long labelNo, @RequestBody UpdateLabelRQ updateLabelRQ) {
        User user = SessionUtils.getSessionUser().toUser();

        labelService.updateLabel(labelNo, updateLabelRQ, user);

        return Result.<Void>builder()
            .code(200)
            .message("ok")
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
        User user = SessionUtils.getSessionUser().toUser();

        labelService.deleteLabel(labelNo, user);

        return Result.<Void>builder()
            .code(200)
            .message("ok")
            .build();
    }
}
