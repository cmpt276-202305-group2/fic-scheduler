package com.group2.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group2.server.model.BlockType;

public interface BlockTypeRepository extends JpaRepository<BlockType, Integer> {
    BlockType findByName(String name);
}
