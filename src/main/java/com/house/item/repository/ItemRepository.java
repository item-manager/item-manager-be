package com.house.item.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.house.item.entity.Item;
import com.house.item.entity.Location;
import com.house.item.entity.User;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    List<Item> findByUser(User user);

    List<Item> findByLocation(Location location);
}
