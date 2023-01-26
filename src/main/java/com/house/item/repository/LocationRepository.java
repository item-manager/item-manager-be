package com.house.item.repository;

import com.house.item.entity.Location;

import java.util.Optional;

public interface LocationRepository {

    Long save(Location location);

    Optional<Location> findOne(Long locationNo);

    Optional<Location> findByLocationNoAndUserNo(Long locationNo, Long userNo);
}
