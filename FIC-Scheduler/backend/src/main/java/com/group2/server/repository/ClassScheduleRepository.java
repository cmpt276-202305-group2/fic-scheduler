package com.group2.server.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.ClassSchedule;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Integer> {
    Set<ClassSchedule> findBySemester(String semester);
}
