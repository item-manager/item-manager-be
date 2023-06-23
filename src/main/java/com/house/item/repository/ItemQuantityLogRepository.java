package com.house.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.house.item.entity.ItemQuantityLog;

public interface ItemQuantityLogRepository
	extends JpaRepository<ItemQuantityLog, Long>, ItemQuantityLogRepositoryCustom {
}