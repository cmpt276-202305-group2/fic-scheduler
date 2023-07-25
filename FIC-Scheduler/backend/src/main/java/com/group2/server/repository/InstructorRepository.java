package com.group2.server.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.*;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
    Set<Instructor> findByName(String name);
}
