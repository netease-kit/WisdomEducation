package com.netease.util.gson;

import com.google.gson.*;
import com.netease.util.StringKit;

import java.lang.reflect.Type;

public class ByteArrayTypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {

    @Override
    public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(StringKit.deserialize(src));
    }

    @Override
    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return StringKit.serialize(json.getAsString());
    }
}
