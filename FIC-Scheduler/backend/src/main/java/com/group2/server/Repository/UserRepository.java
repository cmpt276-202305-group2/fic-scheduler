package com.group2.server.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.Model.ApplicationUser;


public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    ApplicationUser findByUsername(String username);
}
