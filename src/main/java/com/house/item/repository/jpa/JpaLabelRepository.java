package com.house.item.repository.jpa;

import com.house.item.entity.Label;
import com.house.item.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaLabelRepository implements LabelRepository {

    private final EntityManager em;
    private static final String SELECT_FROM_JPQL = "select l from Label l";

    @Override
    public void save(Label label) {
        em.persist(label);
    }

    @Override
    public Optional<Label> findOne(Long labelNo) {
        return Optional.ofNullable(em.find(Label.class, labelNo));
    }

    @Override
    public Optional<Label> findByLabelNoAndUserNo(Long labelNo, Long userNo) {
        String jpql = SELECT_FROM_JPQL +
                " join fetch l.user u" +
                " where l.labelNo = :labelNo and u.userNo = :userNo";
        List<Label> labels = em.createQuery(jpql, Label.class)
                .setParameter("labelNo", labelNo)
                .setParameter("userNo", userNo)
                .getResultList();
        return labels.stream().findAny();
    }

    @Override
    public Optional<Label> findByNameAndUserNo(String name, Long userNo) {
        String jpql = SELECT_FROM_JPQL +
                " join fetch l.user u" +
                " where l.name = :name and u.userNo = :userNo";
        List<Label> labels = em.createQuery(jpql, Label.class)
                .setParameter("name", name)
                .setParameter("userNo", userNo)
                .getResultList();
        return labels.stream().findAny();
    }

    @Override
    public List<Label> findByUserNo(Long userNo) {
        String jpql = SELECT_FROM_JPQL +
                " join fetch l.user u" +
                " where u.userNo = :userNo";
        return em.createQuery(jpql, Label.class)
                .setParameter("userNo", userNo)
                .getResultList();
    }

    @Override
    public void deleteByLabelNo(Long labelNo) {
        em.remove(em.find(Label.class, labelNo));
    }
}
