package com.group2.server.repository;

import com.group2.server.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
    Instructor findByName(String name);
}
