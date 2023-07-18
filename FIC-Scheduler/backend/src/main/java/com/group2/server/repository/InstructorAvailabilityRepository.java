package com.group2.server.repository;

import com.group2.server.model.InstructorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface InstructorAvailabilityRepository extends JpaRepository<InstructorAvailability, Integer> {
    Set<InstructorAvailability> findByInstructor_Name(String instructorName);
}
