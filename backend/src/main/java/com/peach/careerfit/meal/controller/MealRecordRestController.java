package com.peach.careerfit.meal.controller;

import java.time.LocalDate;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.jwt.JwtUtils;
import com.peach.careerfit.meal.model.dto.MealRecord;
import com.peach.careerfit.meal.model.service.MealRecordService;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/api/meal/record")
@RestController
public class MealRecordRestController {
	
	private MealRecordService mealRecordService;
	private JwtUtils jwtUtils;
	public MealRecordRestController(MealRecordService mealRecordService, JwtUtils jwtUtils) {
		this.mealRecordService = mealRecordService;
		this.jwtUtils = jwtUtils;
	}
	
	@GetMapping("/{date}")
	public ResponseEntity<Object> getMealRecord(@PathVariable LocalDate date, HttpServletRequest request) {
		MealRecord mealRecord = new MealRecord();
		mealRecord.setDate(date);
		String token = jwtUtils.getAccessToken(request);
		mealRecord.setUserId(jwtUtils.getUserIdFromToken(token));
		try {
			List<MealRecord> mealRecords = mealRecordService.getMealRecordByUserId(mealRecord);
			System.out.println(mealRecord);
			if(mealRecords.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("해당 날짜의 회원의 식단기록이 없습니다.");
			}
			return ResponseEntity.status(HttpStatus.OK).body(mealRecords);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
		}
	}
	
	@PostMapping
	public ResponseEntity<Object> registMealRecord(@RequestPart(name="mealRecord") MealRecord mealRecord, // JSON 데이터를 Java 객체로 받음
												   @RequestPart(name="file", required=false) MultipartFile file) {
		try {
			int status = mealRecordService.registMealRecord(mealRecord, file);
			if(status == 0) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 식단기록 등록 처리중 문제가 발생했습니다.");				
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 식단기록 등록 처리중 문제가 발생했습니다.");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body("회원 식단기록이 등록되었습니다.");
	}
	
	@PutMapping
	public ResponseEntity<Object> updateMealRecord(@RequestPart(name="meal-record") MealRecord mealRecord,
												   @RequestPart(name="file", required = false) MultipartFile file){
		try {
			int status = mealRecordService.setMealRecoard(mealRecord, file);
			if(status == 0) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 식단기록 수정 처리중 문제가 발생했습니다.");				
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 식단기록 수정 처리중 문제가 발생했습니다.");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body("회원 식단기록이 수정되었습니다.");
	}
	
	@DeleteMapping("/{mealRecordId}")
	public ResponseEntity<Object> deleteMealRecord(@PathVariable("mealRecordId") int mealRecordId) {
		try {
			int status = mealRecordService.removeMealRecord(mealRecordId);
			if(status == 0) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 식단기록 삭제 처리중 문제가 발생했습니다.");				
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 식단기록 삭제 처리중 문제가 발생했습니다.");				
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원 식단기록이 삭제되었습니다.");
	}
}
