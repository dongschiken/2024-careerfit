package com.peach.careerfit.gpt.model.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GptChatHistoryService {
	
	private final RedisTemplate<String, Object> redisTemplate;
    private static final int MAX_HISTORY = 20; // 최대 메시지 개수	
    
    public GptChatHistoryService(@Qualifier("chatRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
  	/**
  	 * GPT 챗 히스토리 메시지 1개를 레디스에 저장
  	 * @param userId
  	 * @param message
  	 */
    public void saveMessage(String userId, String message) {
        String key = "gpt_chat_history:" + userId;
//        System.out.println(message);
        // 메시지 추가
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire("gpt_chat_history:" + userId, 20, TimeUnit.MINUTES); // 10분 후 자동 삭제
        Long size = redisTemplate.opsForList().size(key);
        System.out.println(size);
//        if (size != null && size > MAX_HISTORY) {
//            redisTemplate.opsForList().trim(key, size - MAX_HISTORY, size - 1);
//        }
    }
    
    /**
     * GPT 챗 히스토리 레디스에서 전부 삭제
     * @param userId
     */
    public void removeAllMessages(String userId) {
    	String key = "gpt_chat_history:" + userId;
    	redisTemplate.delete(key);
    }
    
    /**
     * GPT 챗 히스토리 레디스에서 전부 조회
     * @param userId
     * @return
     */
    public List<Object> getAllMessages(String userId) {
    	String key = "gpt_chat_history:" + userId;
    	return redisTemplate.opsForList().range(key, 0, -1);
    }
}
