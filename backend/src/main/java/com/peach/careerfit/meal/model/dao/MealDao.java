package com.peach.careerfit.meal.model.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.peach.careerfit.meal.model.dto.Meal;

@Mapper
public interface MealDao {
	int insertUserMeals(List<Meal> meals);
	int deleteUserMeals(int userId, Set<String> dates);
	List<Meal> selectUserMeals(@Param("userId") int userId, @Param("date") String date);
}
