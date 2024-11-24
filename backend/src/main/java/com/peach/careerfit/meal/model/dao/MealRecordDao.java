package com.peach.careerfit.meal.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.meal.model.dto.MealBodyRecordResponse;
import com.peach.careerfit.meal.model.dto.MealRecord;

@Mapper
public interface MealRecordDao {
	int insertMealRecord(MealRecord mealRecord);
	List<MealRecord> selectMealRecordByUserId(MealRecord mealRecord);
	int deleteMealRecord(int mealRecordId);
	int updateMealRecord(MealRecord mealRecord);
	int countMealRecord(MealRecord mealRecord);
	List<MealBodyRecordResponse> selectDateMealBodyRecord(int userId);
}
