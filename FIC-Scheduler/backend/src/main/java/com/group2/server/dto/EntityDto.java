package com.group2.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EntityDtoDeserializer.class)
public interface EntityDto {
    Integer getId();

    void setId(Integer id);

}
