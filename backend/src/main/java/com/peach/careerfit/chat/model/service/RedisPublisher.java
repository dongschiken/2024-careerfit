//package com.peach.careerfit.chat.model.service;
//
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class RedisPublisher {
//
//	@Qualifier(value = "chatRedisTemplate")
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final ChannelTopic topic;
//
//    public void publish(Object message) {
//        redisTemplate.convertAndSend(topic.getTopic(), message);
//    }
//}
