package com.peach.careerfit.meal.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.jwt.JwtResponse;
import com.peach.careerfit.meal.model.dto.MealBodyRecordResponse;
import com.peach.careerfit.meal.model.dto.MealRecord;
import com.peach.careerfit.meal.model.service.MealRecordService;
import com.peach.careerfit.user.model.dto.ResponseTokenUser;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/api/meal/record")
@RestController
public class MealRecordRestController {
	
	private final MealRecordService mealRecordService;
	private final JwtResponse jwtResponse;
	public MealRecordRestController(MealRecordService mealRecordService, JwtResponse jwtResponse) {
		this.mealRecordService = mealRecordService;
		this.jwtResponse = jwtResponse;
	}
	
	@GetMapping
	public ResponseEntity<Object> getMealBodyRecords(HttpServletRequest request) {
		ResponseTokenUser user = jwtResponse.extractTokenUser(request);
		try {
			List<MealBodyRecordResponse> response = mealRecordService.getMealBodyRecordByUser(user.getUserId());
			System.out.println(response);
			if(response.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원의 식단기록이 없습니다.");
			}
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
		}
	}
	
	@GetMapping("/{date}")
	public ResponseEntity<Object> getMealRecord(@PathVariable("date") String date, HttpServletRequest request) {
		ResponseTokenUser user = jwtResponse.extractTokenUser(request);
		MealRecord mealRecord = new MealRecord();
		mealRecord.setUserId(user.getUserId());
		mealRecord.setDate(date);
		System.out.println("mealRecord" + mealRecord);
		try {
			List<MealRecord> mealRecords = mealRecordService.getMealRecordByUserId(mealRecord);
			System.out.println(mealRecords);
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
												   @RequestPart(name="file", required = false) MultipartFile file,
												   HttpServletRequest request) {
//		if(!mealRecord.getDate().equals(LocalDate.now().toString())) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//		}
		ResponseTokenUser user = jwtResponse.extractTokenUser(request);
		mealRecord.setUserId(user.getUserId());
		
		try {
			int status = mealRecordService.registMealRecord(mealRecord, file);
			if(status == 0) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 식단기록 등록 처리중 문제가 발생했습니다.");				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 식단기록 등록 처리중 문제가 발생했습니다.");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body("회원 식단기록이 등록되었습니다.");
	}
	
	@PutMapping
	public ResponseEntity<Object> updateMealRecord(@RequestPart(name="mealRecord") MealRecord mealRecord,
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
	
	@GetMapping("/streak/{date}")
	public ResponseEntity<Object> getMealStreak(@PathVariable("date") String date, HttpServletRequest request) {
		try {
			ResponseTokenUser user = jwtResponse.extractTokenUser(request);
			MealRecord mealRecord = MealRecord.builder().
										userId(user.getUserId())
										.date(date)
										.build();
			Integer streak = mealRecordService.getMealStreakByUser(mealRecord);
			int totalStreak = mealRecordService.getMealStreakTotal(user.getUserId());
			Map<String, Object> response = new HashMap<>();
			response.put("streak", streak);
			response.put("totalStreak", totalStreak);
			System.out.println("response"+response);
			if(streak == null) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
			}
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
