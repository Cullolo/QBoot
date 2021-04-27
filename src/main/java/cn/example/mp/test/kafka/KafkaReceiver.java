package cn.example.mp.test.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 消息接受者
 * @Description: TODO
 * @Author: xianpei.qin
 * @Date: 2020/5/13 14:18
 **/
@Component
public class KafkaReceiver {


    @KafkaListener(topics = "test")
    public void listen(ConsumerRecord<?,?> record){
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()){

            String msg = (String) kafkaMessage.get();

            System.out.println(msg);

        }

    }

}
