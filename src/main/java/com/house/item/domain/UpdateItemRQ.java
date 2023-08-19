package com.house.item.domain;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import com.house.item.entity.ItemType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateItemRQ {
	@NotBlank
	private String name;
	@NotNull
	private ItemType type;
	@NotNull
	private Long locationNo;
	private String photoName;
	@Builder.Default
	@Range(min = 0, max = 5)
	private Integer priority = 0;
	private Integer threshold;
	private String memo;
	private List<Long> labels;
}
