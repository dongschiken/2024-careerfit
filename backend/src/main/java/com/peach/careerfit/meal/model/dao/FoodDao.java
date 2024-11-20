package com.peach.careerfit.meal.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.meal.model.dto.Food;

@Mapper
public interface FoodDao {

	public List<Food> getAllFoods();

}
