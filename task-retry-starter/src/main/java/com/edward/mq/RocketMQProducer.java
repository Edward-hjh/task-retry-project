package com.edward.mq;

import com.edward.mq.codec.MessageSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @Author: hejh
 * @Date: 2023/3/28 16:05
 */
@Component
@Slf4j
public class RocketMQProducer {

    private DefaultMQProducer producer = new DefaultMQProducer();

    public boolean sendMsg(String topic, String keys, String msg) {
        if (!StringUtils.isEmpty(keys)) {
            return send(topic, keys, true, msg);
        }
        return send(topic, keys, false, msg);
    }
    private boolean send(String topic, String keys, boolean order, Object obj) {
        String tags = null;
        Message msg = new Message(topic, tags, keys, MessageSerializer.serialize(obj));

        SendResult sendResult;
        try {
            if (order) {
                Assert.notNull(keys, "The key must not be null, when you send orderly message.");
                sendResult = this.producer.send(msg, new SelectMessageQueueByHash(), keys);
            } else {
                sendResult = this.producer.send(msg);
            }
        } catch (Exception var) {
            log.error(String.format("The RocketMQ producer message failed.The topic：%s", topic), var);
            return false;
        }
        log.info("The rocketmq send result is:{}", sendResult);
        return SendStatus.SEND_OK.equals(sendResult.getSendStatus());
    }




}
