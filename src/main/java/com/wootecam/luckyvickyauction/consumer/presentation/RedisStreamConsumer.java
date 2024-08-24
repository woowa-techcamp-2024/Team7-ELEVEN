package com.wootecam.luckyvickyauction.consumer.presentation;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final RedisOperator redisOperator;
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer;
    private Subscription subscription;
    private String streamKey;
    private String consumerGroupName;
    private String consumerName;

    @PostConstruct
    public void init() throws InterruptedException {
        // Stream 기본 정보
        this.streamKey = "mystream";
        this.consumerGroupName = "consumerGroupName";
        this.consumerName = "consumerName";

        // Consumer Group 설정
        this.redisOperator.createStreamConsumerGroup(streamKey, consumerGroupName);

        // StreamMessageListenerContainer 설정
        this.listenerContainer = this.redisOperator.createStreamMessageListenerContainer();

        //Subscription 설정
        this.subscription = this.listenerContainer.receive(
                Consumer.from(this.consumerGroupName, consumerName),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                this
        );

        // 2초 마다, 정보 GET
        this.subscription.await(Duration.ofSeconds(2));

        // redis listen 시작
        this.listenerContainer.start();
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        System.out.println("MessageId: " + message.getId());
        System.out.println("Stream: " + message.getStream());
        System.out.println("Body: " + message.getValue());
    }

    @PreDestroy
    public void destroy() {
        if (this.subscription != null) {
            this.subscription.cancel();
        }
        if (this.listenerContainer != null) {
            this.listenerContainer.stop();
        }
    }
}
