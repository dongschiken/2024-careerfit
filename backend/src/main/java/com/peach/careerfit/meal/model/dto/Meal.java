package com.peach.careerfit.meal.model.dto;

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
	private int userId;
	private String date;
	private String type;
	private String name;
	private int kcal;
	private double protein;
	private double fat;
	private double carbs;
}
