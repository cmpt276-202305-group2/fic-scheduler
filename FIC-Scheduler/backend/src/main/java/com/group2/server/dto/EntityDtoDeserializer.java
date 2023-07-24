package com.group2.server.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class EntityDtoDeserializer extends StdDeserializer<EntityDto> {

    public EntityDtoDeserializer() {
        this(null);
    }

    public EntityDtoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public EntityDto deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);
        JsonNode idNode = node.findValue("id");

        EntityReferenceDto dto = new EntityReferenceDto();
        if (idNode != null) {
            dto.setId(idNode.asInt());
        }
        return dto;
    }
}