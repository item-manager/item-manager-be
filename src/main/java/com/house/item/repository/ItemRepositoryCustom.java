package com.house.item.repository;

import java.util.List;

import com.house.item.domain.ConsumableItemDTO;
import com.house.item.domain.ConsumableSearch;
import com.house.item.domain.EquipmentSearch;
import com.house.item.entity.Item;
import com.house.item.entity.Location;

public interface ItemRepositoryCustom {

	List<Item> findByRoom(Location room);

	List<ConsumableItemDTO> findConsumableByNameAndLabel(ConsumableSearch consumableSearch);

	int getConsumableRowCount(ConsumableSearch consumableSearch);

	List<Item> findEquipmentByNameAndLabelAndPlace(EquipmentSearch equipmentSearch);

	int getEquipmentRowCount(EquipmentSearch equipmentSearch);
}
