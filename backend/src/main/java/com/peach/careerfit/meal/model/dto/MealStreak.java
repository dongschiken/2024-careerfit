package com.peach.careerfit.meal.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealStreak {
	private int mealStreakId;
	private int userId;
	private int streak;
	private byte morning;
	private byte lunch;
	private byte dinner;
}
