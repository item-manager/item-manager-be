package com.house.item.repository;

import java.util.List;

import org.springframework.data.domain.Page;

import com.house.item.domain.ConsumableItemDTO;
import com.house.item.domain.ConsumableSearch;
import com.house.item.domain.EquipmentSearch;
import com.house.item.entity.Item;
import com.house.item.entity.Location;

public interface ItemRepositoryCustom {

	List<Item> findByRoom(Location room);

	Page<ConsumableItemDTO> findConsumableByNameAndLabel(ConsumableSearch consumableSearch);

	Page<Item> findEquipmentByNameAndLabelAndPlace(EquipmentSearch equipmentSearch);
}
