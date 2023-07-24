package com.group2.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.Classroom;

public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {
    Classroom findByRoomNumber(String roomNumber);
}
