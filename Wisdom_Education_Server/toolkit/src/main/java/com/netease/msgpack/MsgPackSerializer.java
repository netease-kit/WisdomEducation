package com.netease.msgpack;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;

public class MsgPackSerializer implements Serializer<Object> {

    private final ObjectMapper mapper;

    public MsgPackSerializer() {
        this(new ObjectMapper(new MessagePackFactory())
                .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    public MsgPackSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public byte[] serialize(Object source) throws SerializationException {
        if (source == null) {
            return ByteArrayKit.EMPTY;
        }

        try {
            return mapper.writeValueAsBytes(source);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] source) throws SerializationException {
        return deserialize(source, Object.class);
    }

    public <T> T deserialize(byte[] source, Class<T> type) throws SerializationException {
        if (type == null) {
            throw new IllegalArgumentException("Deserialization type must not be null!");
        }

        if (ByteArrayKit.isEmpty(source)) {
            return null;
        }

        try {
            return mapper.readValue(source, type);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    public static void main(String[] args) {
        MsgPackSerializer s = new MsgPackSerializer();
        byte[] bytes = s.serialize("韩国国会");

        System.out.println(s.deserialize(bytes));
    }
}
