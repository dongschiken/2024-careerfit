package com.peach.careerfit.meal.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealBodyRecordResponse {
	private String date;
	private int day;
	private int countType;
	private double weight;
	private double skeletalMuscle;
	private int bodyFat;
}