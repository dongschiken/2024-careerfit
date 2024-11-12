package com.peach.careerfit.meal.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Food {
	private int foodId;
	private String name;
	private int kcal;
	private int protein;
	private int fat;
	private int carbohydrates;
}
