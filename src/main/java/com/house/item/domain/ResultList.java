package com.house.item.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResultList<T> {
    private Page page;
    private List<T> data;
}
