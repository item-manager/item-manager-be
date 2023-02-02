package com.house.item.repository.jpa;

import com.house.item.entity.Label;
import com.house.item.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaLabelRepository implements LabelRepository {

    private final EntityManager em;

    @Override
    public void save(Label label) {
        em.persist(label);
    }

    @Override
    public Optional<Label> findOne(Long labelNo) {
        return Optional.ofNullable(em.find(Label.class, labelNo));
    }
}
