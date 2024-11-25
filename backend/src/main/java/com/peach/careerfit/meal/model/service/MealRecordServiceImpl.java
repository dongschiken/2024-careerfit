package com.peach.careerfit.meal.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.body.model.dao.BodyRecordDao;
import com.peach.careerfit.component.FileStorageComponent;
import com.peach.careerfit.meal.model.dao.MealRecordDao;
import com.peach.careerfit.meal.model.dto.MealBodyRecordResponse;
import com.peach.careerfit.meal.model.dto.MealRecord;
import com.peach.careerfit.meal.model.dto.ResponseMealStreakRank;

@Service
public class MealRecordServiceImpl implements MealRecordService {

	private MealRecordDao mealRecordDao;
	private FileStorageComponent fileStorageComponent;
	private static final String type = "Meal";
	public MealRecordServiceImpl(MealRecordDao mealRecordDao, FileStorageComponent fileStorageComponent) {
		this.mealRecordDao = mealRecordDao;
		this.fileStorageComponent = fileStorageComponent;
	}

	@Override
	public int registMealRecord(MealRecord mealRecord, MultipartFile file) {
//		if(mealRecordDao.countMealRecord(mealRecord) > 0) {
//			return 0;
//		}
		System.out.println("여기는");
		if(mealRecordDao.countMealStreak(mealRecord) == 0) {
			// 인서트
			Integer streak = mealRecordDao.selectBeforeMealStreak(mealRecord);
			if(streak == null) streak = 0;
			mealRecordDao.insertMealStreak(mealRecord, streak+1);
		}else {
			// 업데이트
			mealRecordDao.updateMealRecord(mealRecord);
		}
		String img = fileStorageComponent.saveFile(file, type);
		mealRecord.setImg(img);
		return mealRecordDao.insertMealRecord(mealRecord);
	}

	@Override
	public List<MealRecord> getMealRecordByUserId(MealRecord mealRecord) {
		return mealRecordDao.selectMealRecordByUserId(mealRecord);
	}

	@Override
	public int setMealRecoard(MealRecord mealRecord, MultipartFile file) {
		String img = fileStorageComponent.saveFile(file, type);
		mealRecord.setImg(img);
		return mealRecordDao.updateMealRecord(mealRecord);
	}

	@Override
	public int removeMealRecord(int mealRecordId) {
		return mealRecordDao.deleteMealRecord(mealRecordId);
	}

	@Override
	public List<MealBodyRecordResponse> getMealBodyRecordByUser(int userId) {
		return mealRecordDao.selectDateMealBodyRecord(userId);
	}

	@Override
	public Integer getMealStreakByUser(MealRecord mealRecord) {
		Integer streak = mealRecordDao.selectMealStreakByUserId(mealRecord);
		if(streak == null) {
			streak = mealRecordDao.selectBeforeMealStreak(mealRecord);
		}
		return streak;
	}

	@Override
	public int getMealStreakTotal(int userId) {
		return mealRecordDao.selectTotalStreakByUserId(userId);
	}

	@Override
	public List<ResponseMealStreakRank> getMealStreakRank() {
		return mealRecordDao.selectTotalStreakRank();
	}

}
