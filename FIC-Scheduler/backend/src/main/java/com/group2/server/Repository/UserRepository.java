package com.group2.server.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.Model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
}
