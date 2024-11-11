package com.peach.careerfit.meal.model.service;

import java.util.List;

import com.peach.careerfit.meal.model.dto.MealRecord;

public interface MealRecordService {
	int registMealRecord(MealRecord mealRecord);
	List<MealRecord> getMealRecordByUserId(MealRecord mealRecord);
}
