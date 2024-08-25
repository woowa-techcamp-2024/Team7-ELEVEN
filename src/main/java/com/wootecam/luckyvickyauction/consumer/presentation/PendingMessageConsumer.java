package com.wootecam.luckyvickyauction.consumer.presentation;

import com.wootecam.luckyvickyauction.consumer.config.RedisStreamConfig;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class PendingMessageConsumer {

    private final RedisOperator redisOperator;
    private final RedisStreamConfig redisStreamConfig;

    @Scheduled(fixedRate = 1000)
    public void consumePendingMessage() {

        // 처리되지 않은 메시지 조회
        PendingMessages pendingMessageInfos = redisOperator.getPendingMessage(
                redisStreamConfig.getStreamKey(),
                redisStreamConfig.getConsumerGroupName(),
                redisStreamConfig.getConsumerName()
        );

        RecordId[] recordIds = pendingMessageInfos.stream().map(
                PendingMessage::getId
        ).toArray(RecordId[]::new);

        // 처리되지 않은 메시지 데이터 조회
        List<MapRecord<String, Object, Object>> messages = redisOperator.claim(
                redisStreamConfig.getStreamKey(),
                redisStreamConfig.getConsumerGroupName(),
                redisStreamConfig.getConsumerName(),
                Duration.ofMinutes(1),
                recordIds
        );

        // 메시지 처리
        messages.forEach(message -> {
            log.info("MessageId: {}", message.getId());
            log.info("Stream: {}", message.getStream());
            log.info("Body: {}", message.getValue());
            redisOperator.acknowledge(redisStreamConfig.getConsumerGroupName(), message);
        });
    }
}
