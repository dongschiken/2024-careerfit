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
public class MealRecord {
	private int mealRecordId;
	private int userId;
	private String date;
	private String type; // 아침, 점심, 저녁
	private String fullness; // 포만감
	private String img;
	private String mealTime;
	private String content;
}
