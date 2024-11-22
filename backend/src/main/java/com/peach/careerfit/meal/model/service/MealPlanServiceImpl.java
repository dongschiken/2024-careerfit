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
public class MealPlanServiceImpl implements MealPlanService{

	private final MealDao mealDao;
	/*
	 * 
	 * carbs
: 
0
date
: 
"2024-11-23"
fat
: 
15
kcal
: 
210
mealId
: 
43
name
: 
"스크램블 에그 (3개)"
protein
: 
18
type
: 
"아침"
userId
: 
1
	 *
	 */
	
	@Override
	public Map<String, Object> getUserMeals(ResponseTokenUser user, String date) {
		Map<String, Object> response = new HashMap<>();
		List<Meal> meals = mealDao.selectUserMeals(user.getUserId(), date);
		ResponseMeal responseMeals = new ResponseMeal();
		responseMeals.setDinner(new Meal());
		responseMeals.setLunch(new Meal());
		responseMeals.setMorning(new Meal());
		responseMeals.setDate(date);
		responseMeals.setUser(user);
		System.out.println(meals.size());
		// 아침, 점심, 저녁 기리 묶어서 처리
		for (int i = 0; i < meals.size(); i++) {
			System.out.println(meals.get(i).getType());
			switch (meals.get(i).getType()) {
				case "아침":
//					System.out.println("아침");
					Meal morning = responseMeals.getMorning();
					calculate(morning, meals.get(i));
					break;
				case "점심":
//					System.out.println("점심");
					Meal lunch = responseMeals.getLunch();
					calculate(lunch, meals.get(i));
					break;
				case "저녁":
//					System.out.println("저녁");
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
		try {
			typeMeal.setName(typeMeal.getName() + ", " + meal.getName());
			typeMeal.setKcal(typeMeal.getKcal() + meal.getKcal());
			typeMeal.setFat(typeMeal.getFat() + meal.getFat());
			typeMeal.setProtein(typeMeal.getProtein() + meal.getProtein());			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
