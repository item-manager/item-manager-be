package com.house.item.repository;

import com.house.item.entity.User;

import java.util.Optional;

public interface UserRepository {
    Long save(User user);

    Optional<User> findOne(Long id);

    Optional<User> findById(String id);

    void delete(User user);
}
