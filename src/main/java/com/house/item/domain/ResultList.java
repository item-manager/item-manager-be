package com.house.item.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResultList<T> {
	private PageRS page;
	private List<T> data;
}
