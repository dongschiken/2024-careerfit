package com.peach.careerfit.body.model.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.body.model.dao.BodyRecordDao;
import com.peach.careerfit.body.model.dto.BodyRecord;
import com.peach.careerfit.component.FileStorageComponent;

@Service
public class BodyRecordServiceImpl implements BodyRecordService{
	private final BodyRecordDao bodyRecordDao;
	private final FileStorageComponent fileStorageComponent;
	private static final String type = "Body";
	public BodyRecordServiceImpl(BodyRecordDao bodyRecordDao, FileStorageComponent fileStorageComponent) {
		this.bodyRecordDao = bodyRecordDao;
		this.fileStorageComponent = fileStorageComponent;
	}
	
	@Override
	public int registBodyRecord(BodyRecord bodyRecord, MultipartFile file) {
		if(bodyRecordDao.countBodyRecord(bodyRecord) > 0) {
			return 0;
		}
		String img = fileStorageComponent.saveFile(file, type);
		System.out.println("여기까진 들어오나??");
		bodyRecord.setImg(img);
		return bodyRecordDao.insertBodyRecord(bodyRecord);
	}
	
	@Override
	public int setBodyRecord(BodyRecord bodyRecord, MultipartFile file) {
		String img = fileStorageComponent.saveFile(file, type);
		bodyRecord.setImg(img);
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
