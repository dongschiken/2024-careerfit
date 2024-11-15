package com.peach.careerfit.auth.model.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {
	
	private final StringRedisTemplate stringRedisTemplate;
	
	public RefreshTokenService(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}
	
	/**
	 * Refresh Token 저장
	 * @param email
	 * @param refreshToken
	 * @param duration
	 */
    public void saveRefreshToken(String email, String refreshToken, long duration) {
    	stringRedisTemplate.opsForValue().set(email, refreshToken, duration, TimeUnit.MILLISECONDS);
    }

    /**
     * Refresh Token 가져오기
     * @param email
     * @return
     */
    public String getRefreshToken(String email) {
        return stringRedisTemplate.opsForValue().get(email);
    }

    /**
     * Refresh Token 삭제 (로그아웃 시 사용)
     * @param email
     */
    public void deleteRefreshToken(String email) {
    	stringRedisTemplate.delete(email);
    }

    /**
     * Refresh Token 유효성 검사
     * @param email
     * @param refreshToken
     * @return
     */
    public boolean validateRefreshToken(String email, String refreshToken) {
        String storedToken = getRefreshToken(email);
        return storedToken != null && storedToken.equals(refreshToken);
    }
}
