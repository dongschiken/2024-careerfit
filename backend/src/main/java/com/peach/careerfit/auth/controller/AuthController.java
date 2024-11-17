package com.peach.careerfit.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.peach.careerfit.auth.model.dto.EmailRequest;
import com.peach.careerfit.user.model.service.EmailService;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

 // 인증 코드 저장 로직
    @PostMapping("/send-email-verification")
    public ResponseEntity<String> sendEmailVerification(@RequestBody EmailRequest emailRequest) {
        try {
//        	System.out.println(emailRequest);
            String verificationCode = generateVerificationCode();
            String subject = "CAREER FIT 이메일 인증 코드";
            String body = "인증 코드: " + verificationCode;

            // 이메일 발송
            emailService.sendVerificationEmail(emailRequest.getEmail(), subject, body);

            // Redis에 인증 코드 저장 (유효기간 5분)
            String redisKey = "verificationCode:" + emailRequest.getEmail();
            redisTemplate.opsForValue().set(redisKey, verificationCode, 5, TimeUnit.MINUTES);
//            System.out.println(redisKey);
//            System.out.println(verificationCode);
//            System.out.println(redisTemplate.opsForValue().get(redisKey));
            return ResponseEntity.ok("이메일 전송 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("이메일 전송에 실패했습니다.");
        }
    }

    // 인증 코드 검증 로직
    @PostMapping("/verify-email-code")
    public ResponseEntity<Map<String, Boolean>> verifyEmailCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        // Redis에서 인증 코드 조회
        String redisKey = "verificationCode:" + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        boolean isValid = storedCode != null && storedCode.equals(code);
        System.out.println(storedCode);
        System.out.println(email);
        System.out.println(code);
        return ResponseEntity.ok(Collections.singletonMap("isValid", isValid));
    }


    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6자리 인증 코드 생성
        return String.valueOf(code);
    }
}
