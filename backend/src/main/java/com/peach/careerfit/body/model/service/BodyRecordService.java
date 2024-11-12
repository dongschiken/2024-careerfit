package com.peach.careerfit.body.model.service;

import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.body.model.dto.BodyRecord;

public interface BodyRecordService {
	int registBodyRecord(BodyRecord bodyRecord, MultipartFile file);
	int setBodyRecord(BodyRecord bodyRecord, MultipartFile file);
	int removeBodyRecord(int bodyRecordId);
	BodyRecord getBodyRecord(BodyRecord bodyRecord);
}
