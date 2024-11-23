package com.peach.careerfit.meal.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.component.FileStorageComponent;
import com.peach.careerfit.meal.model.dao.MealRecordDao;
import com.peach.careerfit.meal.model.dto.MealRecord;

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
		if(mealRecordDao.countMealRecord(mealRecord) > 0) {
			return 0;
		}
		String img = fileStorageComponent.saveFile(file, type);
		mealRecord.setImg(img);
		return mealRecordDao.insertMealRecord(mealRecord);
	}

	@Override
	public List<MealRecord> getMealRecordByUserId(MealRecord mealRecord) {
		System.out.println("sasdasdasdasdasd"+mealRecord);
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

}
