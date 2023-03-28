package com.edward.mq.codec;

/**
 * @Author: hejh
 * @Date: 2023/3/28 16:47
 */
public interface MessageSerializer<T> {
    String DEFAULT_CHARSET = "UTF-8";

    byte[] serializeMessage(T var1);

    T deserializeMessage(byte[] var1, Class<T> var2);

    static byte[] serialize(Object msgData) {
        if (msgData != null) {
            if (msgData instanceof String) {
                return (new StringMessageSerializer()).serializeMessage(msgData.toString());
            } else {
                return msgData instanceof byte[] ? (byte[])((byte[])msgData) : (new JsonMessageSerializer()).serializeMessage(msgData);
            }
        } else {
            return null;
        }
    }
}
