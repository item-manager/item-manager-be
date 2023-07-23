package com.house.item.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.house.item.entity.Item;
import com.house.item.entity.Location;
import com.house.item.entity.User;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    @Query("select i from Item i join i.location l where i.itemNo = :itemNo and l.user = :user")
    Optional<Item> findByIdAndUser(@Param("itemNo") Long itemNo, @Param("user") User user);

    @Query("select i from Item i join i.location l where l.user = :user")
    List<Item> findByUser(@Param("user") User user);

    List<Item> findByLocation(Location location);
}
