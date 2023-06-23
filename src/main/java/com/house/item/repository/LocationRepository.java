package com.house.item.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
import com.house.item.entity.User;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByTypeAndUser(LocationType type, User user);

    List<Location> findByRoom(Location room);
}
