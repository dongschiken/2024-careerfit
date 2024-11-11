package com.peach.careerfit.meal.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealFood {
	private int mealFoodId;
	private int mealId;
	private int foodId;
	private int quantity;
}
