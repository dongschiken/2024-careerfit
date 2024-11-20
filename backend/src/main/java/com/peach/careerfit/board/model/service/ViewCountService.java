package com.peach.careerfit.board.model.service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.peach.careerfit.board.model.dao.BoardDao;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

@EnableScheduling // spring에서 스케쥴링 관련 기능을 제공하는 어노테이션
@Service
public class ViewCountService {
	
	
	private final StringRedisTemplate stringRedisTemplate;
	private final BoardDao boardDao;
	
	
	public ViewCountService(StringRedisTemplate stringRedisTemplate, BoardDao boardDao) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.boardDao = boardDao;
	}
	
	/**
	 * 서버 종료시 동기화
	 */
	@PreDestroy
	public void onShitDown() {
		syncViewCountsToDatabase();
	}
	
	/**
	 * boardId에 해당하는 userId가 있는지 확인하고 viewCount를 중복으로 올리지 않고, 없을 경우 증가시키는 로직
	 * 
	 * @param boardId
	 * @param userId
	 * @return
	 */
	public boolean incrementViewCount(Integer boardId, Integer userId) {
		if (userId == 0) {
			return false;
		}
		String viewKey = "board:view:" + boardId + ":userId:" + userId;
		Boolean isViewed = stringRedisTemplate.hasKey(viewKey);
		if (Boolean.TRUE.equals(isViewed)) {
			return false;
		}
		stringRedisTemplate.opsForValue().set(viewKey, String.valueOf(true), 1, TimeUnit.DAYS);
		String viewCountKey = "board:view:" + boardId;
		stringRedisTemplate.opsForValue().increment(viewCountKey);
		return true;
	}
	
	/**
	 * 캐시메모리에 저장된 viewCount를 가져오는 로직
	 * 만약 viewCount에 해당하는 key가 없는 경우 db에서 가져옴
	 * @param boardId
	 * @return
	 */
	public Integer getViewCount(Integer boardId) {
		String viewCountKey = "board:view:" + boardId;
		Object viewCount = stringRedisTemplate.opsForValue().get(viewCountKey);
		if (viewCount != null) {
			return Integer.parseInt(viewCount.toString());
		}

		Integer dbViewCount = boardDao.getViewCount(boardId);
		if (dbViewCount == null) {
			dbViewCount = 0;
		}
		stringRedisTemplate.opsForValue().set(viewCountKey, String.valueOf(dbViewCount), 1, TimeUnit.DAYS);
		return dbViewCount;
	}
	
	/**
	 * 매 20분 마다 db 서버에 데이터 동기화 작업
	 */
	@Scheduled(cron = "0 */20 * * * *") // 매 5분마다 실행
	public void syncViewCountsToDatabase() {
		Set<String> keys = stringRedisTemplate.keys("board:view:*");
		System.out.println(keys);
		if (keys != null) {
	        for (String key : keys) {
	            if (key.contains(":userId:")) {
	            	stringRedisTemplate.delete(key); 
	            } else {
	                // 게시글 조회수 동기화
	                Integer boardId = Integer.parseInt(key.split(":")[2]);
	                Integer viewCount = Integer.parseInt(stringRedisTemplate.opsForValue().get(key).toString());
	                boardDao.updateViewCount(boardId, viewCount); 
	                stringRedisTemplate.delete(key); // Redis에서 삭제
	            }
	        }
	    }
	}
	
	/**
	 * 어떤 작업이 일어났을 때 db에 조회수 동기화 작업 하기위한 코드
	 * @param boardId
	 */
	public void syncViewCountImmediately(Integer boardId) {
		String viewCountKey = "board:view:" + boardId;

		Object count = stringRedisTemplate.opsForValue().get(viewCountKey);
		if (count != null) {
			Integer viewCount = Integer.parseInt(count.toString());
			boardDao.updateViewCount(boardId, viewCount);
			stringRedisTemplate.delete(viewCountKey);
		}
	}
}
