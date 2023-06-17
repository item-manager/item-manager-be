package com.house.item.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.house.item.entity.Label;
import com.house.item.entity.User;

public interface LabelRepository extends JpaRepository<Label, Long> {
    List<Label> findByUser(User user);

    List<Label> findByNameAndUser(String name, User user);
}
