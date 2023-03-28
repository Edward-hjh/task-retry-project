package com.edward.mq.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: hejh
 * @Date: 2023/3/28 16:47
 */
public class JsonMessageSerializer<T> implements MessageSerializer<T> {
    public JsonMessageSerializer() {
    }

    public byte[] serializeMessage(Object data) {
        return JSON.toJSONBytes(data, new SerializerFeature[0]);
    }

    public T deserializeMessage(byte[] data, Class<T> clazz) {
        return JSON.parseObject(data, clazz, new Feature[0]);
    }

    public List<T> deserializeListMessage(byte[] data, Class<T> clazz) {
        String result = new String(data, StandardCharsets.UTF_8);
        return result.startsWith("{") && result.endsWith("}") ? Arrays.asList(this.deserializeMessage(data, clazz)) : JSON.parseArray(result, clazz);
    }
}
