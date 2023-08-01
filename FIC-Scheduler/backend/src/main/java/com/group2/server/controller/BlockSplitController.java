package com.group2.server.controller;

import com.group2.server.dto.*;
import com.group2.server.model.*;
import com.group2.server.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class BlockSplitController {

    @Autowired
    private BlockRequirementSplitRepository blockRequirementSplitRepository;

    @GetMapping("/block-splits")
    public ResponseEntity<List<BlockRequirementSplitDto>> readListByQuery() {
        try {
            return new ResponseEntity<>(blockRequirementSplitRepository.findAll().stream().map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/block-splits/{id}")
    public ResponseEntity<BlockRequirementSplitDto> readOneById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(toDto(blockRequirementSplitRepository.findById(id).get()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/block-splits")
    public ResponseEntity<List<BlockRequirementSplitDto>> createOrUpdateList(
            @RequestBody List<BlockRequirementSplitDto> blockRequirementSplitDtoList) {
        try {
            return new ResponseEntity<>(
                    blockRequirementSplitDtoList.stream().map(this::createOrUpdateFromDto).map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/block-splits/{id}")
    public ResponseEntity<BlockRequirementSplitDto> updateOneById(@PathVariable Integer id,
            @RequestBody BlockRequirementSplitDto blockRequirementSplitDto) {
        try {
            if ((blockRequirementSplitDto.getId() != null) && !id.equals(blockRequirementSplitDto.getId())) {
                throw new IllegalArgumentException();
            }
            blockRequirementSplitDto.setId(id);
            return new ResponseEntity<>(toDto(createOrUpdateFromDto(blockRequirementSplitDto)), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/block-splits/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Integer id) {
        try {
            blockRequirementSplitRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public BlockRequirementSplitDto toDto(BlockRequirementSplit blockRequirementSplit) {
        return new BlockRequirementSplitDto(blockRequirementSplit.getId(), blockRequirementSplit.getName(),
                blockRequirementSplit.getBlocks().stream()
                        .map(b -> new BlockRequirementDto(b.getRoomType(), b.getDuration()))
                        .toList());
    }

    public BlockRequirementSplit createOrUpdateFromDto(BlockRequirementSplitDto blockRequirementSplitDto) {
        List<BlockRequirement> blockRequirements = null;
        BlockRequirementSplit blockRequirementSplit;

        if (blockRequirementSplitDto.getBlocks() != null) {
            blockRequirements = blockRequirementSplitDto.getBlocks().stream()
                    .map(b -> new BlockRequirement(null, b.getRoomType(), b.getDuration())).toList();
        }

        if (blockRequirementSplitDto.getId() != null) {
            blockRequirementSplit = blockRequirementSplitRepository.findById(blockRequirementSplitDto.getId()).get();
            if (blockRequirementSplitDto.getName() != null) {
                blockRequirementSplit.setName(blockRequirementSplitDto.getName());
            }
            if (blockRequirements != null) {
                blockRequirementSplit.setBlocks(blockRequirements);
            }
        } else {
            blockRequirementSplit = new BlockRequirementSplit(null, blockRequirementSplitDto.getName(),
                    blockRequirements);
        }
        return blockRequirementSplitRepository.save(blockRequirementSplit);
    }
}
