package com.group2.server.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.Model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
