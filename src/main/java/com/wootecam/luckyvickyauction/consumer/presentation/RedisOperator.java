package com.wootecam.luckyvickyauction.consumer.presentation;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandKeyword;
import io.lettuce.core.protocol.CommandType;
import java.time.Duration;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisOperator {

    private final RedisConnectionFactory redisConnectionFactory;
    private final StringRedisTemplate redisTemplate;


    public boolean isStreamConsumerGroupExist(String streamKey, String consumerGroupName) {
        Iterator<StreamInfo.XInfoGroup> iterator = this.redisTemplate
                .opsForStream().groups(streamKey).stream().iterator();

        while (iterator.hasNext()) {
            StreamInfo.XInfoGroup xInfoGroup = iterator.next();
            if (xInfoGroup.groupName().equals(consumerGroupName)) {
                return true;
            }
        }
        return false;
    }

    public void createStreamConsumerGroup(String streamKey, String consumerGroupName) {
        // if stream is not exist, create stream and consumer group of it
        if (Boolean.FALSE.equals(this.redisTemplate.hasKey(streamKey))) {
            RedisAsyncCommands commands = (RedisAsyncCommands) redisConnectionFactory
                    .getConnection()
                    .getNativeConnection();

            CommandArgs<String, String> args = new CommandArgs<>(StringCodec.UTF8)
                    .add(CommandKeyword.CREATE)
                    .add(streamKey)
                    .add(consumerGroupName)
                    .add("0")
                    .add("MKSTREAM");

            commands.dispatch(CommandType.XGROUP, new StatusOutput(StringCodec.UTF8), args);
        }
        // stream is exist, create consumerGroup if is not exist
        else {
            if (!isStreamConsumerGroupExist(streamKey, consumerGroupName)) {
                this.redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.from("0"), consumerGroupName);
            }
        }
    }

    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> createStreamMessageListenerContainer() {
        return StreamMessageListenerContainer.create(redisConnectionFactory,
                StreamMessageListenerContainer
                        .StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofMillis(20))
                        .build()
        );
    }

}
