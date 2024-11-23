package com.peach.careerfit.body.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.body.model.dto.BodyRecord;

@Mapper
public interface BodyRecordDao {
	int insertBodyRecord(BodyRecord bodyRecord);
	int updateBodyRecord(BodyRecord bodyRecord);
	int deleteBodyRecordById(int BodyRecordId);
	BodyRecord selectBodyRecordByDate(BodyRecord bodyRecord);
	int countBodyRecord(BodyRecord bodyRecord);
}
