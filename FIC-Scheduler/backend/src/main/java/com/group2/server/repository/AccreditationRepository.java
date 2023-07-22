package com.group2.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.Accreditation;

public interface AccreditationRepository extends JpaRepository<Accreditation, Integer> {
    Accreditation findByName(String name);
}
