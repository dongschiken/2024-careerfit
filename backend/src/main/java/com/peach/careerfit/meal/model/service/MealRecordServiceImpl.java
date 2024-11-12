package com.peach.careerfit.meal.model.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.file.component.FileStorageComponent;
import com.peach.careerfit.meal.model.dao.MealRecordDao;
import com.peach.careerfit.meal.model.dto.MealRecord;

@Service
public class MealRecordServiceImpl implements MealRecordService {

	private MealRecordDao mealRecordDao;
	private FileStorageComponent fileStorageComponent;
	public MealRecordServiceImpl(MealRecordDao mealRecordDao) {
		this.mealRecordDao = mealRecordDao;
	}

	@Override
	public int registMealRecord(MealRecord mealRecord, MultipartFile file) {
//		mealRecord.setImg();
		
		return mealRecordDao.insertMealRecord(mealRecord);
	}

	@Override
	public List<MealRecord> getMealRecordByUserId(MealRecord mealRecord) {
		return mealRecordDao.selectMealRecordByUserId(mealRecord);
	}

	@Override
	public int setMealRecoard(MealRecord mealRecord, MultipartFile file) {
		if (file != null) {
//			validateFile(file);
			String originName = file.getOriginalFilename();
			if (!file.isEmpty() && originName.length() > 0) {
				try {
					String subDir = new SimpleDateFormat("yyyy/MM/dd/HH/").format(new Date()).toString();
					File dir = new File("c:/uploads/" + subDir);
					dir.mkdirs();
					String systemName = UUID.randomUUID().toString() + originName;
					File f = new File(dir, systemName);
					file.transferTo(f);
					mealRecord.setImg(dir.toString() + systemName);
					System.out.println(mealRecord);
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		return mealRecordDao.updateMealRecord(mealRecord);
	}

	@Override
	public int removeMealRecord(int mealRecordId) {
		return mealRecordDao.deleteMealRecord(mealRecordId);
	}

}
