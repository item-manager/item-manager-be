package com.house.item.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.User;

public interface ItemQuantityLogRepository
	extends JpaRepository<ItemQuantityLog, Long>, ItemQuantityLogRepositoryCustom {

	@Query("select DISTINCT iql.mall from ItemQuantityLog iql where iql.item.user = :user and iql.mall is not null")
	List<String> findDistinctMalls(@Param("user") User user);
}