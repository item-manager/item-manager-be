package com.house.item.repository;

import com.house.item.entity.User;

import java.util.Optional;

public interface UserRepository {
    Long save(User user);

    Optional<User> findOne(Long id);

    Optional<User> findById(String id);

    Optional<User> findByIdAndPassword(String id, String password);

    void delete(User user);
}
