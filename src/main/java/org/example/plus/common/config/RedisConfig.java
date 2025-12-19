package org.example.plus.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 추가 Java 8에서 추가된 LocalDate, LocalDateTime을 커버하기 위한 설정
        // ObjectMapper 커스터마이징
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .activateDefaultTyping(
                        LaissezFaireSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.PROPERTY
                );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(mapper);


        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
        //자바에서는 Redis에 값을 넣을 때는 자바에서 사용하는 타입을 그대로 사용 못한다.
        //Long, int, String, List 등등등...
        // Redis 는 어떤타입으로 보내는 문자열로 저장한다.

        // 자바의 특정 타입으로 보냈는데  -> Redis에 문자열로 저장합니다.
        // 다시 자바로 받을때 타입 불일치가 일어난다.
        // Json 파싱해주는 설정을 넣어줘야 한다.