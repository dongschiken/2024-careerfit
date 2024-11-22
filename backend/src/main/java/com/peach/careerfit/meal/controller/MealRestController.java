package com.peach.careerfit.meal.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peach.careerfit.jwt.JwtResponse;
import com.peach.careerfit.meal.model.dto.Meal;
import com.peach.careerfit.meal.model.service.MealPlanService;
import com.peach.careerfit.user.model.dto.ResponseTokenUser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/meal")
@RequiredArgsConstructor
public class MealRestController {
	
	private final MealPlanService mealPlanService;
	private final JwtResponse jwtResponse;
	
	@GetMapping("/{date}")
	public ResponseEntity<Object> getMeal(@PathVariable("date") String date, HttpServletRequest request) {
		ResponseTokenUser user = jwtResponse.extractTokenUser(request);
		Map<String, Object> response = mealPlanService.getUserMeals(user, date);
		try {
			if(response.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body(user);
			}else {
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("식단 데이터 ");
		}
	}
	
}
