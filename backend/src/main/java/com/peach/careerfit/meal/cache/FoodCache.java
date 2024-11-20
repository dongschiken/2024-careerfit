package com.peach.careerfit.meal.cache;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.peach.careerfit.meal.model.dao.FoodDao;
import com.peach.careerfit.meal.model.dto.Food;

import jakarta.annotation.PostConstruct;

@Component
public class FoodCache {
	
	private final RedisTemplate<String, Food> redisTemplate;
	private final FoodDao foodDao;
	
	public FoodCache(RedisTemplate<String, Food> redisTemplate, FoodDao foodDao) {
		this.redisTemplate = redisTemplate;
		this.foodDao = foodDao;
	}
	
	/**
	 * 어플리케이션 시작할때 음식 정보 전부 가져와서 초기화
	 */
	@PostConstruct
	public void initializeRedis() {
		List<Food> foods = foodDao.getAllFoods();
		for (Food food : foods) {
			redisTemplate.opsForHash().put("foods", food.getName(), food); // key, HashKey, value 순서
		}
		System.out.println("레디스에 초기화 : " + foods.size());
	}
	
	public List<Object> getAllFoods() {
		return redisTemplate.opsForHash().values("foods");
	}
	
	public Food getFoodByName(String foodName) {
		Food food = (Food)redisTemplate.opsForHash().get("foods", foodName);
		if(food == null) {
			throw new RuntimeException("해당이름의 음식이 없습니다.");
		}
		return food;
	}
	
}
