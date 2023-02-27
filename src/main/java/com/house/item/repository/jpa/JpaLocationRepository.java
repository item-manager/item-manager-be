package com.house.item.repository.jpa;

import com.house.item.entity.Location;
import com.house.item.entity.LocationType;
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
    private static final String SELECT_FROM_JPQL = "select l from Location l";

    @Override
    public void save(Location location) {
        em.persist(location);
    }

    @Override
    public Optional<Location> findOne(Long locationNo) {
        return Optional.ofNullable(em.find(Location.class, locationNo));
    }

    @Override
    public Optional<Location> findByLocationNoAndUserNo(Long locationNo, Long userNo) {
        String jpql = SELECT_FROM_JPQL +
                " join fetch l.user u" +
                " where l.locationNo = :locationNo and u.userNo = :userNo";
        List<Location> locations = em.createQuery(jpql, Location.class)
                .setParameter("locationNo", locationNo)
                .setParameter("userNo", userNo)
                .getResultList();
        return locations.stream().findAny();
    }

    @Override
    public List<Location> findByTypeAndUserNo(LocationType type, Long userNo) {
        String jpql = SELECT_FROM_JPQL +
                " join fetch l.user u" +
                " where l.type = :type and u.userNo = :userNo";
        return em.createQuery(jpql, Location.class)
                .setParameter("type", type)
                .setParameter("userNo", userNo)
                .getResultList();
    }

    @Override
    public List<Location> findByRoom(Long roomNo) {
        String jpql = SELECT_FROM_JPQL +
                " join fetch l.room r" +
                " where r.locationNo = :roomNo";
        return em.createQuery(jpql, Location.class)
                .setParameter("roomNo", roomNo)
                .getResultList();
    }

    @Override
    public void delete(Location location) {
        em.remove(location);
    }
}
