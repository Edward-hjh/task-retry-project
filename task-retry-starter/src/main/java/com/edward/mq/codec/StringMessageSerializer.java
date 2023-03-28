package com.edward.mq.codec;

/**
 * @Author: hejh
 * @Date: 2023/3/28 16:48
 */
public class StringMessageSerializer implements MessageSerializer<String> {
    public StringMessageSerializer() {
    }

    public byte[] serializeMessage(String data) {
        try {
            return data.getBytes("UTF-8");
        } catch (Exception var3) {
            throw new RuntimeException("serialize string message error.", var3);
        }
    }

    public String deserializeMessage(byte[] data, Class<String> clazz) {
        try {
            return new String(data, "UTF-8");
        } catch (Exception var4) {
            throw new RuntimeException("deserialize string message error.", var4);
        }
    }
}
