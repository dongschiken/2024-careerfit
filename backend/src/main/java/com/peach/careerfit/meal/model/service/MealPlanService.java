package com.peach.careerfit.meal.model.service;

import java.util.Map;

import com.peach.careerfit.user.model.dto.ResponseTokenUser;

public interface MealPlanService {
	
	Map<String, Object> getUserMeals(ResponseTokenUser user, String date);
}
