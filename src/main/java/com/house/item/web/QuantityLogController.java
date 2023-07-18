package com.house.item.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.house.item.domain.QuantityLogDTO;
import com.house.item.domain.QuantityLogMallRS;
import com.house.item.domain.QuantityLogRS;
import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogSumByDate;
import com.house.item.domain.QuantityLogSumSearch;
import com.house.item.domain.QuantityLogSumsRQ;
import com.house.item.domain.QuantityLogSumsRS;
import com.house.item.domain.QuantityLogsRQ;
import com.house.item.domain.QuantityLogsServiceRQ;
import com.house.item.domain.QuantityTypeRS;
import com.house.item.domain.Result;
import com.house.item.domain.ResultList;
import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.QuantityType;
import com.house.item.entity.User;
import com.house.item.service.QuantityLogService;
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
        User user = SessionUtils.getSessionUser().toUser();

        Pageable pageable = PageRequest.of(quantityLogsRQ.getPage() - 1, quantityLogsRQ.getSize(),
            quantityLogsRQ.getSort().equals("+") ? Sort.Direction.ASC : Sort.Direction.DESC,
            quantityLogsRQ.getOrderBy());

        QuantityLogsServiceRQ request = QuantityLogsServiceRQ.builder()
            .itemNo(quantityLogsRQ.getItemNo())
            .type(quantityLogsRQ.getType())
            .year(quantityLogsRQ.getYear())
            .month(quantityLogsRQ.getMonth())
            .build();

        QuantityLogSearch quantityLogSearch = quantityLogService.getQuantityLogSearch(request, pageable, user);
        Page<QuantityLogDTO> logs = quantityLogService.getItemQuantityLogs(quantityLogSearch);

        List<QuantityLogRS> quantityLogRSList = new ArrayList<>();
        for (QuantityLogDTO log : logs) {
            ItemQuantityLog quantityLog = log.getQuantityLog();

            quantityLogRSList.add(
                QuantityLogRS.builder()
                    .quantityLogNo(quantityLog.getItemQuantityLogNo())
                    .type(QuantityTypeRS.fromType(quantityLog.getType()))
                    .date(quantityLog.getDate())
                    .count(quantityLog.getCount())
                    .price(quantityLog.getPrice())
                    .unitPrice(log.getUnitPrice())
                    .mall(quantityLog.getMall())
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
        User user = SessionUtils.getSessionUser().toUser();

        QuantityLogSumSearch quantityLogSumSearch = quantityLogService.getQuantityLogSumSearch(quantityLogSumsRQ, user);
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

    @Operation(summary = "구매처 목록 조회")
    @GetMapping("/malls")
    public Result<List<QuantityLogMallRS>> getQuantityLogMalls() {
        User user = SessionUtils.getSessionUser().toUser();

        List<QuantityLogMallRS> malls = quantityLogService.getQuantityLogDistinctMalls(user);

        return Result.<List<QuantityLogMallRS>>builder()
            .data(malls)
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
        User user = SessionUtils.getSessionUser().toUser();

        quantityLogService.deleteQuantityLog(quantityLogNo, user);

        return Result.<Void>builder()
            .code(200)
            .message("ok")
            .build();
    }
}
