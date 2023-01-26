package com.house.item.repository.jpa;

import com.house.item.entity.Location;
import com.house.item.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaLocationRepository implements LocationRepository {

    private final EntityManager em;

    @Override
    public Long save(Location location) {
        em.persist(location);
        return location.getLocationNo();
    }

    @Override
    public Optional<Location> findOne(Long locationNo) {
        return Optional.ofNullable(em.find(Location.class, locationNo));
    }

    @Override
    public Optional<Location> findByLocationNoAndUserNo(Long locationNo, Long userNo) {
        String jpql = "select l from Location l" +
                " join fetch l.user u" +
                " where l.locationNo = :locationNo and u.userNo = :userNo";
        List<Location> locations = em.createQuery(jpql, Location.class)
                .setParameter("locationNo", locationNo)
                .setParameter("userNo", userNo)
                .getResultList();
        return locations.stream().findAny();
    }
}
