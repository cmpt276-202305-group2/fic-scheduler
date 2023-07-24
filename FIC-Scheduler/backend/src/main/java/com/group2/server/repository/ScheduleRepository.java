package com.group2.server.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.*;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    Set<Schedule> findBySemester(String semester);
}
