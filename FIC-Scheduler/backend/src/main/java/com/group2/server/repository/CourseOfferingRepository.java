package com.group2.server.repository;

import com.group2.server.model.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Integer> {
    CourseOffering findByCourseNumber(String courseNumber);
}
