package com.peach.careerfit.meal.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.peach.careerfit.meal.model.dao.MealRecordDao;
import com.peach.careerfit.meal.model.dto.MealRecord;

@Service
public class MealRecordServiceImpl implements MealRecordService {
	
	private MealRecordDao mealRecordDao;
	public MealRecordServiceImpl(MealRecordDao mealRecordDao) {
		this.mealRecordDao = mealRecordDao;
	}
	@Override
	public int registMealRecord(MealRecord mealRecord) {
		return mealRecordDao.insertMealRecord(mealRecord);
	}
	
	@Override
	public List<MealRecord> getMealRecordByUserId(MealRecord mealRecord) {
		return mealRecordDao.selectMealRecordByUserId(mealRecord);
	}
	
}
