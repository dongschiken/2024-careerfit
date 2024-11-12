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
public class Meal {
	private int mealId;
	private int mealPlanId;
	private LocalDate date;
	private String type; // 아침, 점심, 저녁
}
