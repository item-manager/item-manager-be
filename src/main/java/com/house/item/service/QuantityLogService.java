package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.ConsumeItemRQ;
import com.house.item.domain.PurchaseItemRQ;
import com.house.item.domain.QuantityLogSearch;
import com.house.item.domain.QuantityLogsRQ;
import com.house.item.entity.Item;
import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.QuantityType;
import com.house.item.exception.SubtractCountExceedItemQuantityException;
import com.house.item.repository.ItemQuantityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuantityLogService {
    private final ItemQuantityLogRepository quantityLogRepository;
    private final ItemService itemService;

    @Transactional
    public int purchaseItem(Long itemNo, PurchaseItemRQ purchaseItemRQ) {
        Item item = itemService.getItem(itemNo);

        ItemQuantityLog quantityLog = ItemQuantityLog.builder()
                .item(item)
                .type(QuantityType.PURCHASE)
                .mall(purchaseItemRQ.getMall())
                .date(purchaseItemRQ.getDate())
                .price(purchaseItemRQ.getUnitPrice())
                .count(purchaseItemRQ.getCount())
                .build();
        quantityLogRepository.save(quantityLog);

        item.addQuantity(purchaseItemRQ.getCount());

        return item.getQuantity();
    }

    @Transactional
    public int consumeItem(Long itemNo, ConsumeItemRQ consumeItemRQ) {
        Item item = itemService.getItem(itemNo);
        if (item.getQuantity() < consumeItemRQ.getCount()) {
            throw new SubtractCountExceedItemQuantityException(ExceptionCodeMessage.SUBTRACT_COUNT_EXCEEDED_ITEM_QUANTITY_EXCEPTION.message());
        }

        ItemQuantityLog quantityLog = ItemQuantityLog.builder()
                .item(item)
                .type(QuantityType.CONSUME)
                .date(consumeItemRQ.getDate())
                .count(consumeItemRQ.getCount())
                .build();
        quantityLogRepository.save(quantityLog);

        item.subtractQuantity(consumeItemRQ.getCount());

        return item.getQuantity();
    }

    public QuantityLogSearch getQuantityLogSearch(QuantityLogsRQ quantityLogsRQ) {
        Item item = itemService.getItem(quantityLogsRQ.getItemNo());

        Map<String, String> sortMapping = new HashMap<>();
        sortMapping.put("+", "ASC");
        sortMapping.put("-", "DESC");

        return QuantityLogSearch.builder()
                .item(item)
                .type(quantityLogsRQ.getType())
                .year(quantityLogsRQ.getYear())
                .month(quantityLogsRQ.getMonth())
                .orderBy(quantityLogsRQ.getOrderBy().getColumn())
                .sort(sortMapping.get(quantityLogsRQ.getSort()))
                .page(quantityLogsRQ.getPage())
                .size(quantityLogsRQ.getSize())
                .build();
    }

    public List<ItemQuantityLog> getItemQuantityLogs(QuantityLogSearch quantityLogSearch) {
        return quantityLogRepository.findByItemNoAndTypeAndYearAndMonth(quantityLogSearch);
    }
}
