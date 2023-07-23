package com.house.item.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.house.item.entity.ItemQuantityLog;
import com.house.item.entity.User;

public interface ItemQuantityLogRepository
	extends JpaRepository<ItemQuantityLog, Long>, ItemQuantityLogRepositoryCustom {

	@Query("select iql from ItemQuantityLog iql join iql.item i join i.location l where iql.itemQuantityLogNo = :id and l.user = :user")
	Optional<ItemQuantityLog> findByIdAndUser(@Param("id") Long itemQuantityLogNo, @Param("user") User user);

	@Query("select DISTINCT iql.mall from ItemQuantityLog iql join iql.item i join i.location l where l.user = :user and iql.mall is not null")
	List<String> findDistinctMalls(@Param("user") User user);
}