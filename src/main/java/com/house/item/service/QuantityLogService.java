package com.house.item.service;

import com.house.item.common.ExceptionCodeMessage;
import com.house.item.domain.PurchaseItemRQ;
import com.house.item.domain.SubtractItemQuantityRQ;
import com.house.item.entity.Item;
import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.QuantityType;
import com.house.item.exception.SubtractCountExceedItemQuantityException;
import com.house.item.repository.ItemQuantityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public int subtractItemQuantity(Long itemNo, SubtractItemQuantityRQ subtractItemQuantityRQ) {
        Item item = itemService.getItem(itemNo);
        if (item.getQuantity() < subtractItemQuantityRQ.getCount()) {
            throw new SubtractCountExceedItemQuantityException(ExceptionCodeMessage.SUBTRACT_COUNT_EXCEEDED_ITEM_QUANTITY_EXCEPTION.message());
        }

        ItemQuantityLog quantityLog = ItemQuantityLog.builder()
                .item(item)
                .type(QuantityType.CONSUME)
                .date(subtractItemQuantityRQ.getDate())
                .count(subtractItemQuantityRQ.getCount())
                .build();
        quantityLogRepository.save(quantityLog);

        item.subtractQuantity(subtractItemQuantityRQ.getCount());

        return item.getQuantity();
    }
}
