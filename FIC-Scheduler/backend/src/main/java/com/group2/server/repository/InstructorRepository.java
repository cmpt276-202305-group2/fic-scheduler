package com.group2.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.*;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
}
