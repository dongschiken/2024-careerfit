package com.peach.careerfit.body.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyRecord {
	private int bodyRecordId;
	private int userId;
	private String date;
	private double weight;
	private double skeletalMuscle; // 골격근
	private int bodyFat;	   // 체지방
	private String img;
	private String bodyCondition;
	private String content;
}
