package com.group2.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.*;

public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {
    Optional<ApplicationUser> findByUsername(String username);
}
