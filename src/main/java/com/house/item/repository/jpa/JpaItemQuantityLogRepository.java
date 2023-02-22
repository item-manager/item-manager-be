package com.house.item.repository.jpa;

import com.house.item.entity.ItemQuantityLog;
import com.house.item.repository.ItemQuantityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class JpaItemQuantityLogRepository implements ItemQuantityLogRepository {
    private final EntityManager em;

    @Override
    public void save(ItemQuantityLog itemQuantityLog) {
        em.persist(itemQuantityLog);
    }
}
