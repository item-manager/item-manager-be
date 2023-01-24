package com.house.item.repository.jpa;

import com.house.item.entity.Item;
import com.house.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaItemRepository implements ItemRepository {

    private final EntityManager em;

    @Override
    public Long save(Item item) {
        em.persist(item);
        return item.getItemNo();
    }

    @Override
    public Optional<Item> findOne(Long itemNo) {
        return Optional.ofNullable(em.find(Item.class, itemNo));
    }
}
