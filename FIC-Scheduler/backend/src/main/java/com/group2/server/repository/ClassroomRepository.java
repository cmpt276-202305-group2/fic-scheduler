package com.group2.server.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.*;

public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {
    Set<Classroom> findByRoomNumber(String roomNumber);
}
