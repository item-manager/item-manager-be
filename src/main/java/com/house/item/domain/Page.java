package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Page {
    private int totalDataCnt;
    private int totalPages;
    private int requestPage;
    private int requestSize;
}
