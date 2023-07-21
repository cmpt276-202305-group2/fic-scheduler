package com.group2.server.repository;

import com.group2.server.model.Facilities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facilities, Integer> {
    Facilities findByName(String name);
}
