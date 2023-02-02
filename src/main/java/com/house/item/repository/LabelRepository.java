package com.house.item.repository;

import com.house.item.entity.Label;

import java.util.Optional;

public interface LabelRepository {
    void save(Label label);

    Optional<Label> findOne(Long labelNo);
}
