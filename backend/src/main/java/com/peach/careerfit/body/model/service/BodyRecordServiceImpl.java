package com.peach.careerfit.body.model.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.body.model.dao.BodyRecordDao;
import com.peach.careerfit.body.model.dto.BodyRecord;

@Service
public class BodyRecordServiceImpl implements BodyRecordService{
	private BodyRecordDao bodyRecordDao;
	private static final int maxSize = 5 * 1024 * 1024;
	
	public BodyRecordServiceImpl(BodyRecordDao bodyRecordDao) {
		this.bodyRecordDao = bodyRecordDao;
	}
	@Override
	public int registBodyRecord(BodyRecord bodyRecord, MultipartFile file) {
		
		return bodyRecordDao.insertBodyRecord(bodyRecord);
	}
	@Override
	public int setBodyRecord(BodyRecord bodyRecord, MultipartFile file) {
		return bodyRecordDao.updateBodyRecord(bodyRecord);
	}
	@Override
	public int removeBodyRecord(int bodyRecordId) {
		return bodyRecordDao.deleteBodyRecordById(bodyRecordId);
	}
	@Override
	public BodyRecord getBodyRecord(BodyRecord bodyRecord) {
		return bodyRecordDao.selectBodyRecordByDate(bodyRecord);
	}
	
}
