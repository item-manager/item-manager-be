package com.house.item.repository;

import com.house.item.entity.Location;
import com.house.item.entity.LocationType;

import java.util.List;
import java.util.Optional;

public interface LocationRepository {

    void save(Location location);

    Optional<Location> findOne(Long locationNo);

    Optional<Location> findByLocationNoAndUserNo(Long locationNo, Long userNo);

    List<Location> findByTypeAndUserNo(LocationType type, Long userNo);

    List<Location> findByRoom(Long roomNo);
}
