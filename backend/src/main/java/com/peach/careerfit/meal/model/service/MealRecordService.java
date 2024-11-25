package com.peach.careerfit.meal.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.meal.model.dto.MealBodyRecordResponse;
import com.peach.careerfit.meal.model.dto.MealRecord;
import com.peach.careerfit.meal.model.dto.ResponseMealStreakRank;

public interface MealRecordService {
	int registMealRecord(MealRecord mealRecord, MultipartFile file);
	int setMealRecoard(MealRecord mealRecord, MultipartFile file);
	int removeMealRecord(int mealRecordId);
	List<MealRecord> getMealRecordByUserId(MealRecord mealRecord);
	List<MealBodyRecordResponse> getMealBodyRecordByUser(int userId);
	Integer getMealStreakByUser(MealRecord mealRecord);
	int getMealStreakTotal(int userId);
	List<ResponseMealStreakRank> getMealStreakRank();
}
