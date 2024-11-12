package com.peach.careerfit.body.controller;

import java.net.http.HttpRequest;
import java.time.LocalDate;

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

import com.peach.careerfit.body.model.dto.BodyRecord;
import com.peach.careerfit.body.model.service.BodyRecordService;
import com.peach.careerfit.jwt.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/api/body/record")
@RestController
public class BodyRecordRestController {
	private BodyRecordService bodyRecordService;
	private JwtUtils jwtUtils;

	public BodyRecordRestController(BodyRecordService bodyRecordService, JwtUtils jwtUtils) {
		this.bodyRecordService = bodyRecordService;
		this.jwtUtils = jwtUtils;
	}

	@GetMapping("/{date}")
	public ResponseEntity<Object> getBodyRecord(@PathVariable("date") LocalDate date, HttpServletRequest httpRequest) {
		try {
			BodyRecord bodyRecord = new BodyRecord().builder().date(date).build();
			bodyRecord.setUserId(jwtUtils.getUserIdFromToken(jwtUtils.getAccessToken(httpRequest)));
			BodyRecord userBodyRecord = bodyRecordService.getBodyRecord(bodyRecord);
			if (userBodyRecord == null) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원의 식단기록이 없습니다.");
			}
			return ResponseEntity.status(HttpStatus.OK).body(userBodyRecord);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body("회원의 신체 기록을 가져오는 중 오류 발생");
		}
	}

	@PostMapping
	public ResponseEntity<Object> registBodyRecord(@RequestPart("bodyRecord") BodyRecord bodyRecord,
			@RequestPart("file") MultipartFile file) {
		System.out.println(file);
		System.out.println(bodyRecord);
		try {
			int status = bodyRecordService.registBodyRecord(bodyRecord, file);
			System.out.println(status);
			if (status == 0) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원의 신체 기록을 등록하는 중 오류 발생");
			}
			return ResponseEntity.status(HttpStatus.CREATED).body("회원의 신체 기록 등록 완료");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원의 신체 기록을 등록하는 중 오류 발생");
		}
	}

	@PutMapping
	public ResponseEntity<Object> setBodyRecord(@RequestPart("bodyRecord") BodyRecord bodyRecord,
			@RequestPart("file") MultipartFile file) {
		try {
			int status = bodyRecordService.setBodyRecord(bodyRecord, file);
			if (status == 0) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원의 신체  수정하는 중 오류 발생");
			}
			return ResponseEntity.status(HttpStatus.CREATED).body("회원의 신체 기록 수정 완료");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원의 신체 기록을 수영하는중 오류 발생");
		}
	}

	@DeleteMapping("/{bodyRecordId}")
	public ResponseEntity<Object> removeBodyRecord(@PathVariable("bodyRecordId") int bodyRecordId) {
		try {
			int status = bodyRecordService.removeBodyRecord(bodyRecordId);
			if(status == 0) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원의 신체 기록 삭제 중 오류 발생");
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원의 신체 기록 삭제 완료");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원의 신체 기록 삭제 중 오류 발생");
		}
	}
}
