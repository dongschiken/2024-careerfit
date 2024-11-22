package com.peach.careerfit.meal.model.dto;

import com.peach.careerfit.user.model.dto.ResponseTokenUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMeal {
	private ResponseTokenUser user;
	private String date;
	private Meal morning;
	private Meal lunch;
	private Meal dinner;
}
