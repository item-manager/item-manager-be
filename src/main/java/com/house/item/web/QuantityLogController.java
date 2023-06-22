package com.house.item.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.ErrorResult;
import com.house.item.domain.PageRS;
import com.house.item.domain.QuantityLogRS;
import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumByDate;
import com.house.item.domain.QuantityLogSumSearch;
import com.house.item.domain.QuantityLogSumsRQ;
import com.house.item.domain.QuantityLogSumsRS;
import com.house.item.domain.QuantityLogsRQ;
import com.house.item.domain.QuantityTypeRS;
import com.house.item.domain.Result;
import com.house.item.domain.ResultList;
import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.QuantityType;
import com.house.item.service.QuantityLogService;

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
@RequestMapping("/quantity-logs")
public class QuantityLogController {

    private final QuantityLogService quantityLogService;

    @ApiResponse(
        responseCode = "400",
        content = @Content(
            schema = @Schema(implementation = ErrorResult.class),
            examples = {
                @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM)
            }
        )
    )
    @Operation(summary = "구매, 사용 기록 목록 조회")
    @GetMapping
    public ResultList<QuantityLogRS> getQuantityLogs(@Validated @ModelAttribute QuantityLogsRQ quantityLogsRQ) {
        QuantityLogSearch quantityLogSearch = quantityLogService.getQuantityLogSearch(quantityLogsRQ);
        Page<ItemQuantityLog> logs = quantityLogService.getItemQuantityLogs(quantityLogSearch);

        List<QuantityLogRS> quantityLogRSList = new ArrayList<>();
        for (ItemQuantityLog log : logs) {
            quantityLogRSList.add(
                QuantityLogRS.builder()
                    .quantityLogNo(log.getItemQuantityLogNo())
                    .type(QuantityTypeRS.fromType(log.getType()))
                    .date(log.getDate())
                    .count(log.getCount())
                    .price(log.getPrice())
                    .mall(log.getMall())
                    .build()
            );
        }

        PageRS quantityLogsPageRS = PageRS.builder()
            .totalDataCnt((int)logs.getTotalElements())
            .totalPages(logs.getTotalPages())
            .requestPage(logs.getPageable().getPageNumber() + 1)
            .requestSize(logs.getPageable().getPageSize())
            .build();

        return ResultList.<QuantityLogRS>builder()
            .page(quantityLogsPageRS)
            .data(quantityLogRSList)
            .build();
    }

    @ApiResponse(
        responseCode = "400",
        content = @Content(
            schema = @Schema(implementation = ErrorResult.class),
            examples = {
                @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM)
            }
        )
    )
    @Operation(summary = "구매, 사용 수량 통계 조회")
    @GetMapping("/sums")
    public Result<QuantityLogSumsRS> getQuantityLogSums(
        @Validated @ModelAttribute QuantityLogSumsRQ quantityLogSumsRQ) {
        QuantityLogSumSearch quantityLogSumSearch = quantityLogService.getQuantityLogSumSearch(quantityLogSumsRQ);
        Map<QuantityType, List<QuantityLogSumByDate>> sums = quantityLogService.getItemQuantityLogSumByDate(
            quantityLogSumSearch);

        return Result.<QuantityLogSumsRS>builder()
            .data(
                QuantityLogSumsRS.builder()
                    .logSumByType(sums)
                    .build()
            )
            .build();
    }

    @ApiResponse(
        responseCode = "400",
        content = @Content(
            schema = @Schema(implementation = ErrorResult.class),
            examples = {
                @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.NON_EXISTENT_ITEM_QUANTITY_LOG),
                @ExampleObject(name = ExceptionCodeMessage.SwaggerDescription.SUBTRACT_COUNT_EXCEEDED_ITEM_QUANTITY_EXCEPTION)
            }
        )
    )
    @Operation(summary = "구매, 사용 기록 제거")
    @DeleteMapping("/{quantityLogNo}")
    public Result<Void> deleteQuantityLog(@PathVariable Long quantityLogNo) {
        quantityLogService.deleteQuantityLog(quantityLogNo);

        return Result.<Void>builder()
                .code(200)
                .message("ok")
                .build();
    }
}
