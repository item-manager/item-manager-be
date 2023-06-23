package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PageRS {
	private int totalDataCnt;
	private int totalPages;
	private int requestPage;
	private int requestSize;
}
