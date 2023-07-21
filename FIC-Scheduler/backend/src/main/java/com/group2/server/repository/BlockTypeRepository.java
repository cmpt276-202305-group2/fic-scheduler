package com.group2.server.repository;

import com.group2.server.model.BlockType;
import org.springframework.data.jpa.repository.JpaRepository;



public interface BlockTypeRepository extends JpaRepository<BlockType, Integer> {
    BlockType findByName(String name);
}
