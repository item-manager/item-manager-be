package com.house.item.service;

import com.house.item.domain.AddItemQuantityRQ;
import com.house.item.entity.Item;
import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.QuantityType;
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
    public int addItemQuantity(Long itemNo, AddItemQuantityRQ addItemQuantityRQ) {
        Item item = itemService.getItem(itemNo);

        ItemQuantityLog quantityLog = ItemQuantityLog.builder()
                .item(item)
                .type(QuantityType.PURCHASE)
                .mall(addItemQuantityRQ.getMall())
                .date(addItemQuantityRQ.getDate())
                .price(addItemQuantityRQ.getUnitPrice())
                .count(addItemQuantityRQ.getCount())
                .build();
        quantityLogRepository.save(quantityLog);

        item.addQuantity(addItemQuantityRQ.getCount());

        return item.getQuantity();
    }
}
