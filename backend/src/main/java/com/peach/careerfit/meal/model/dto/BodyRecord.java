package com.peach.careerfit.meal.model.dto;

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
	private LocalDate date;
	private double weight;
	private int skeletalMucle; // 골격근
	private int bodyFat;	   // 체지방
	private String img;
}
