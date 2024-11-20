package com.peach.careerfit.meal.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Food implements Serializable{
	private static final long serialVersionUID = 1L;
	private int foodId;
	private String name;
	private int kcal;
	private int protein;
	private int fat;
	private int carbohydrates;
}
