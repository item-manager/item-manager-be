package com.house.item.repository.jpa;

import com.house.item.entity.Item;
import com.house.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaItemRepository implements ItemRepository {

    private final EntityManager em;

    @Override
    public void save(Item item) {
        em.persist(item);
    }

    @Override
    public Optional<Item> findOne(Long itemNo) {
        String jpql = "select i from Item i" +
                " join fetch i.location p" +
                " join fetch p.room r" +
                " where i.itemNo = :itemNo";
        List<Item> items = em.createQuery(jpql, Item.class)
                .setParameter("itemNo", itemNo)
                .getResultList();
        return items.stream().findAny();
    }

    @Override
    public Optional<Item> findByItemNoAndUserNo(Long itemNo, Long userNo) {
        String jpql = "select i from Item i" +
                " join fetch i.user u" +
                " join fetch i.location p" +
                " join fetch p.room r" +
                " where i.itemNo = :itemNo" +
                " and u.userNo = :userNo";
        List<Item> items = em.createQuery(jpql, Item.class)
                .setParameter("itemNo", itemNo)
                .setParameter("userNo", userNo)
                .getResultList();
        return items.stream().findAny();
    }
}
