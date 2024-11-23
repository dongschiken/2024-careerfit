package com.peach.careerfit.meal.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.peach.careerfit.meal.model.dao.MealDao;
import com.peach.careerfit.meal.model.dto.Meal;
import com.peach.careerfit.meal.model.dto.ResponseMeal;
import com.peach.careerfit.user.model.dto.ResponseTokenUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MealPlanServiceImpl implements MealPlanService {

	private final MealDao mealDao;

	@Override
	public Map<String, Object> getUserMeals(ResponseTokenUser user, String date) {
		Map<String, Object> response = new HashMap<>();
		List<Meal> meals = mealDao.selectUserMeals(user.getUserId(), date);
		ResponseMeal responseMeals = null;
		if(meals.size() != 0) {
			responseMeals = new ResponseMeal();
			responseMeals.setDinner(new Meal());
			responseMeals.setLunch(new Meal());
			responseMeals.setMorning(new Meal());
			responseMeals.setDate(date);
			responseMeals.setUser(user);
		}
		
		for (int i = 0; i < meals.size(); i++) {
			switch (meals.get(i).getType()) {
			case "아침":
				Meal morning = responseMeals.getMorning();
				calculate(morning, meals.get(i));
				break;
			case "점심":
				Meal lunch = responseMeals.getLunch();
				calculate(lunch, meals.get(i));
				break;
			case "저녁":
				Meal dinner = responseMeals.getDinner();
				calculate(dinner, meals.get(i));
				break;
			}
		}
		response.put("meals", responseMeals);
		response.put("user", user);
		return response;
	}

	private void calculate(Meal typeMeal, Meal meal) {
		String currentName = typeMeal.getName() != null ? typeMeal.getName() : "";
		String newName = meal.getName() != null ? meal.getName() : "";
		if (!currentName.isEmpty() && !newName.isEmpty()) {
			currentName += ", ";
		}
		typeMeal.setName(currentName + newName);
		try {
			typeMeal.setKcal(typeMeal.getKcal() + meal.getKcal());
			typeMeal.setFat(typeMeal.getFat() + meal.getFat());
			typeMeal.setCarbs(typeMeal.getCarbs() + meal.getCarbs());
			typeMeal.setProtein(typeMeal.getProtein() + meal.getProtein());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
