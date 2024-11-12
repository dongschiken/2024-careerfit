package com.peach.careerfit.meal.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlan {
	private int mealPlanId;
	private int userId;
	private String mealName;
	private String startDate;
	private String endDate;
	private String goal;
}
