package com.house.item.domain;

import java.util.List;

import com.house.item.entity.Item;
import com.house.item.entity.ItemLabel;
import com.house.item.entity.LocationType;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EquipmentItemRS2 {
	private Long itemNo;
	private int priority;
	private String name;
	private String roomName;
	private String placeName;
	private List<LabelRS> labels;

	public static EquipmentItemRS2 of(Item item) {
		String roomName;
		String placeName = null;
		if (item.getLocation().getType() == LocationType.PLACE) {
			roomName = item.getLocation().getRoom().getName();
			placeName = item.getLocation().getName();
		} else {
			roomName = item.getLocation().getName();
		}

		return EquipmentItemRS2.builder()
			.itemNo(item.getItemNo())
			.priority(item.getPriority())
			.name(item.getName())
			.roomName(roomName)
			.placeName(placeName)
			.labels(
				item.getItemLabels().stream()
					.map(ItemLabel::getLabel)
					.map(LabelRS::of)
					.toList()
			)
			.build();
	}
}
