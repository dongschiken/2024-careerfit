package com.peach.careerfit.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.peach.careerfit.auth.model.dto.EmailRequest;
import com.peach.careerfit.user.model.service.EmailService;
import com.peach.careerfit.user.model.service.UserService;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserService userService;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	// 인증 코드 저장 로직
	@PostMapping("/send-email-verification")
	public ResponseEntity<Map<String, String>> sendEmailVerification(@RequestBody EmailRequest emailRequest) {
		String email = emailRequest.getEmail();

		try {

			// 이메일 중복 여부 확인
			if (!userService.isEmailAvailable(email)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Collections.singletonMap("message", "이미 사용 중인 이메일입니다."));
			}

			String verificationCode = generateVerificationCode();
			String subject = "CAREER FIT 이메일 인증 코드";
			String body = "인증 코드: " + verificationCode;

			// 이메일 발송
			emailService.sendVerificationEmail(emailRequest.getEmail(), subject, body);

			// Redis에 인증 코드 저장 (유효기간 5분)
			String redisKey = "verificationCode:" + emailRequest.getEmail();
			redisTemplate.opsForValue().set(redisKey, verificationCode, 5, TimeUnit.MINUTES);

			 return ResponseEntity.status(HttpStatus.OK)
                     .body(Collections.singletonMap("message", "이메일 인증 코드가 발송되었습니다."));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body(Collections.singletonMap("message", "이메일 인증 코드 전송에 실패했습니다."));
	    }
	}

	// 인증 코드 검증 로직
	@PostMapping("/verify-email-code")
	public ResponseEntity<Map<String, Object>> verifyEmailCode(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String code = request.get("code");

		// Redis에서 인증 코드 조회
		String redisKey = "verificationCode:" + email;
		String storedCode = redisTemplate.opsForValue().get(redisKey);

		if (storedCode == null) {
			// Redis에 저장된 코드가 없는 경우
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("isValid", false, "message", "인증 코드가 만료되었거나 존재하지 않습니다."));
		}

		if (!storedCode.equals(code)) {
			// 인증 코드가 일치하지 않는 경우
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("isValid", false, "message", "인증 코드가 일치하지 않습니다."));
		}

		// 인증 성공
		return ResponseEntity.ok(Map.of("isValid", true, "message", "인증에 성공하였습니다."));
	}

	private String generateVerificationCode() {
		SecureRandom secureRandom = new SecureRandom();
		int code = 100000 + secureRandom.nextInt(900000); // 6자리 인증 코드 생성
		return String.valueOf(code);
	}
}
