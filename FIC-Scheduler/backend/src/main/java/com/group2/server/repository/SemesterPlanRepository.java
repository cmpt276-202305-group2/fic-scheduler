package com.group2.server.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.SemesterPlan;

public interface SemesterPlanRepository extends JpaRepository<SemesterPlan, Integer> {
    Set<SemesterPlan> findBySemester(String semester);
}
