package com.house.item.repository;

import com.house.item.entity.User;

import java.util.Optional;

public interface UserRepository {
    void save(User user);

    Optional<User> findOne(Long userNo);

    Optional<User> findById(String id);

    Optional<User> findByUsername(String username);

    void delete(User user);
}
