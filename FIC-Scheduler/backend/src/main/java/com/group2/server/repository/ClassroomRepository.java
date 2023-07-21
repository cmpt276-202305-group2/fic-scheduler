package com.group2.server.repository;

import com.group2.server.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {
    Classroom findByRoomNumber(String roomNumber);
}
