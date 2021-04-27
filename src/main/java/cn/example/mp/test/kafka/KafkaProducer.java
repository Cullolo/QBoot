package cn.example.mp.test.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息发送者
 * @Description: TODO
 * @Author: xianpei.qin
 * @Date: 2020/5/13 14:06
 **/
@Component
public class KafkaProducer {
    private static Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Value("${my-topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String,String> template;

    public void sendMsg(String msg){
        logger.info(msg);
        template.send(topic,msg);
    }

}
