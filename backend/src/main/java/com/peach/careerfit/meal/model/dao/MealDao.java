package com.peach.careerfit.meal.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.meal.model.dto.Meal;

@Mapper
public interface MealDao {
	int insertUserMeals(List<Meal> meals);
	int deleteUserMeals(int userId);
}
