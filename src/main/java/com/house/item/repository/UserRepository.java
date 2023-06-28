package com.house.item.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.house.item.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(String id);

    Optional<User> findByUsername(String username);
}
