package com.peach.careerfit.meal.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMealStreakRank {
	private int userId;
	private String nickname;
	private String email;
	private int recordCount;
	private int rank;
}
