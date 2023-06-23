package com.house.item.domain;

import java.time.LocalDateTime;

import com.house.item.entity.Item;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class ConsumableItemDTO {
	private Item item;
	private LocalDateTime latestPurchase;
	private LocalDateTime latestConsume;

	@QueryProjection
	public ConsumableItemDTO(Item item, LocalDateTime latestPurchase, LocalDateTime latestConsume) {
		this.item = item;
		this.latestPurchase = latestPurchase;
		this.latestConsume = latestConsume;
	}
}
