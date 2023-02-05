package com.house.item.repository;

import com.house.item.entity.Label;

import java.util.List;
import java.util.Optional;

public interface LabelRepository {
    void save(Label label);

    Optional<Label> findOne(Long labelNo);

    Optional<Label> findByLabelNoAndUserNo(Long labelNo, Long userNo);

    Optional<Label> findByNameAndUserNo(String name, Long userNo);

    List<Label> findByUserNo(Long userNo);

    void deleteByLabelNo(Long labelNo);
}
