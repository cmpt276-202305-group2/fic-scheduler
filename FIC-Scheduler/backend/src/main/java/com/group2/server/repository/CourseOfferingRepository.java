package com.group2.server.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.*;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Integer> {
    Set<CourseOffering> findByName(String name);

    Set<CourseOffering> findByCourseNumber(String courseNumber);
}
