package com.house.item.domain;

import java.time.LocalDateTime;

import com.house.item.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseItemServiceRQ {
	private User user;
	private Long itemId;
	private String mall;
	private LocalDateTime date;
	private int price;
	private int count;
}
