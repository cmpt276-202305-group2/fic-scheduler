package com.group2.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.Facilities;

public interface FacilityRepository extends JpaRepository<Facilities, Integer> {
    Facilities findByName(String name);
}
