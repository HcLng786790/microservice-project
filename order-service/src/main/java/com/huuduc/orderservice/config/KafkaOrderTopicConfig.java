package com.huuduc.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@SuppressWarnings("unused")
@Configuration
public class KafkaOrderTopicConfig {

    // Tạo topic cho kafka
    @Bean
    public NewTopic orderTopic(){
        return TopicBuilder
                .name("order-topic")
                .build();
    }
}
