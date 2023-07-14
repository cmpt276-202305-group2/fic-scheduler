package com.group2.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.Role;

public interface RoleRepository  extends JpaRepository<Role, Integer> {
    Optional<Role> findByAuthority(String authority);
}
