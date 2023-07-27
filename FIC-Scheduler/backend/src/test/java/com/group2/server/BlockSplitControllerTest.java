package com.group2.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.server.controller.BlockSplitController;
import com.group2.server.dto.BlockRequirementSplitDto;
import com.group2.server.model.BlockRequirementSplit;
import com.group2.server.repository.BlockRequirementSplitRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BlockSplitControllerTest {

    @InjectMocks
    private BlockSplitController blockSplitController;

    @Mock
    private BlockRequirementSplitRepository blockRequirementSplitRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(blockSplitController).build();
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testReadListByQuery() throws Exception {
        // Mock the data returned by the repository
        List<BlockRequirementSplit> mockBlockSplits = new ArrayList<>();
        mockBlockSplits.add(new BlockRequirementSplit(1, "Block 1", null));
        mockBlockSplits.add(new BlockRequirementSplit(2, "Block 2", null));
        when(blockRequirementSplitRepository.findAll()).thenReturn(mockBlockSplits);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/block-splits"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Block 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Block 2"));

        // Verify that blockRequirementSplitRepository.findAll() was called once
        verify(blockRequirementSplitRepository, times(1)).findAll();
        verifyNoMoreInteractions(blockRequirementSplitRepository);
    }

    @Test
    public void testReadListByQueryExceptionCase() throws Exception {
        // Mock the blockRequirementSplitRepository to throw an exception when findAll() is called
        when(blockRequirementSplitRepository.findAll()).thenThrow(new RuntimeException("Some error occurred"));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/block-splits"))
                .andExpect(status().isBadRequest());

        // Verify that blockRequirementSplitRepository.findAll() was called once
        verify(blockRequirementSplitRepository, times(1)).findAll();
        verifyNoMoreInteractions(blockRequirementSplitRepository);
    }

    @Test
    public void testReadOneById() throws Exception {
        // Mock the data returned by the repository
        BlockRequirementSplit mockBlockSplit = new BlockRequirementSplit(1, "Block 1", null);
        when(blockRequirementSplitRepository.findById(1)).thenReturn(Optional.of(mockBlockSplit));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/block-splits/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Block 1"));

        // Verify that blockRequirementSplitRepository.findById() was called once with the correct ID
        verify(blockRequirementSplitRepository, times(1)).findById(1);
        verifyNoMoreInteractions(blockRequirementSplitRepository);
    }

    @Test
    public void testReadOneByIdExceptionCase() throws Exception {
        int blockSplitId = 1;

        // Mock the blockRequirementSplitRepository to throw an exception when findById() is called with blockSplitId
        when(blockRequirementSplitRepository.findById(blockSplitId)).thenThrow(new RuntimeException("Block split not found"));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/block-splits/{id}", blockSplitId))
                .andExpect(status().isBadRequest());

        // Verify that blockRequirementSplitRepository.findById() was called once with the correct ID
        verify(blockRequirementSplitRepository, times(1)).findById(blockSplitId);
        verifyNoMoreInteractions(blockRequirementSplitRepository);
    }

    @Test
    public void testCreateOrUpdateList() throws Exception {
        // Mock the data to be sent in the request
        List<BlockRequirementSplitDto> blockRequirementSplitDtoList = Arrays.asList(
                new BlockRequirementSplitDto(null, "Block 1", null),
                new BlockRequirementSplitDto(null, "Block 2", null)
        );

        BlockRequirementSplit savedBlockSplit1 = new BlockRequirementSplit(1, "Block 1", null);
        BlockRequirementSplit savedBlockSplit2 = new BlockRequirementSplit(2, "Block 2", null);

        when(blockRequirementSplitRepository.save(any(BlockRequirementSplit.class)))
                .thenReturn(savedBlockSplit1)
                .thenReturn(savedBlockSplit2);

        // Perform the request and verify the response
        mockMvc.perform(post("/api/block-splits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(blockRequirementSplitDtoList)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Block 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Block 2"));

        // Verify that blockRequirementSplitRepository.save() was called twice with the correct block splits
        verify(blockRequirementSplitRepository, times(2)).save(any(BlockRequirementSplit.class));
        verifyNoMoreInteractions(blockRequirementSplitRepository);
    }

    @Test
    public void testCreateOrUpdateListExceptionCase() throws Exception {
        // Create a list of block split DTOs with null values for required fields
        List<BlockRequirementSplitDto> blockRequirementSplitDtoList = Arrays.asList(
                new BlockRequirementSplitDto(null, null, null),
                new BlockRequirementSplitDto(null, "Block 2", null)
        );

        // Perform the request and verify the response
        mockMvc.perform(post("/api/block-splits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(blockRequirementSplitDtoList)))
                .andExpect(status().isBadRequest());

        // Verify that blockRequirementSplitRepository.save() was not called
        verify(blockRequirementSplitRepository, times(0)).save(any());
    }

    @Test
    public void testUpdateOneById() throws Exception {
        // Mock the updated data
        BlockRequirementSplitDto updatedDto = new BlockRequirementSplitDto(1, "Updated Block 1", null);

        // Mock the block split data in the repository before updating
        BlockRequirementSplit existingBlockSplit = new BlockRequirementSplit(1, "Block 1", null);
        when(blockRequirementSplitRepository.findById(1)).thenReturn(Optional.of(existingBlockSplit));

        // Mock the data returned by the repository after updating
        BlockRequirementSplit updatedBlockSplit = new BlockRequirementSplit(1, "Updated Block 1", null);
        when(blockRequirementSplitRepository.save(any(BlockRequirementSplit.class))).thenReturn(updatedBlockSplit);

        // Perform the request and verify the response
        mockMvc.perform(put("/api/block-splits/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Block 1"));

        // Verify that blockRequirementSplitRepository.findById() was called once with the correct ID
        verify(blockRequirementSplitRepository, times(1)).findById(1);

        // Verify that blockRequirementSplitRepository.save() was called once with the updated block split
        ArgumentCaptor<BlockRequirementSplit> blockSplitCaptor = ArgumentCaptor.forClass(BlockRequirementSplit.class);
        verify(blockRequirementSplitRepository, times(1)).save(blockSplitCaptor.capture());
        verifyNoMoreInteractions(blockRequirementSplitRepository);

        // Verify the updated block split data
        BlockRequirementSplit capturedBlockSplit = blockSplitCaptor.getValue();
        assertEquals(1, capturedBlockSplit.getId());
        assertEquals("Updated Block 1", capturedBlockSplit.getName());
    }

    @Test
    public void testUpdateOneByExceptionCase() throws Exception {
        int blockSplitId = 1;
        BlockRequirementSplitDto blockRequirementSplitDto = new BlockRequirementSplitDto(2, "Block 1", null); // ID 2 mismatches with blockSplitId

        // Perform the request and verify the response
        mockMvc.perform(put("/api/block-splits/{id}", blockSplitId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(blockRequirementSplitDto)))
                .andExpect(status().isBadRequest());

        // Verify that blockRequirementSplitRepository.findById() was not called because of the mismatched IDs
        verify(blockRequirementSplitRepository, times(0)).findById(blockSplitId);
        verifyNoMoreInteractions(blockRequirementSplitRepository);
    }

    @Test
    public void testDeleteOneById() throws Exception {
        int blockSplitId = 1;

        // Perform the request and verify the response
        mockMvc.perform(delete("/api/block-splits/{id}", blockSplitId))
                .andExpect(status().isOk());

        // Verify that blockRequirementSplitRepository.deleteById() was called once with the correct ID
        verify(blockRequirementSplitRepository, times(1)).deleteById(blockSplitId);
        verifyNoMoreInteractions(blockRequirementSplitRepository);
    }

    @Test
    public void testDeleteOneByIdExceptionCase() throws Exception {
        int blockSplitId = 1;

        doThrow(IllegalArgumentException.class).when(blockRequirementSplitRepository).deleteById(any(Integer.class));

        // Perform the request and verify the response
        mockMvc.perform(delete("/api/block-splits/{id}", blockSplitId))
                .andExpect(status().isBadRequest());

        // Verify that blockRequirementSplitRepository.deleteById() was called once with the correct ID
        verify(blockRequirementSplitRepository, times(1)).deleteById(blockSplitId);
        verifyNoMoreInteractions(blockRequirementSplitRepository);
    }
}