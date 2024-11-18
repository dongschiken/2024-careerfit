package com.peach.careerfit.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peach.careerfit.auth.model.service.RefreshTokenService;
import com.peach.careerfit.jwt.JwtUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/refresh-token")
public class RefreshRestController {
	
	private final JwtUtils jwtUtils;
	private final RefreshTokenService refreshTokenService;
	
	public RefreshRestController(JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
		this.jwtUtils = jwtUtils;
		this.refreshTokenService = refreshTokenService;
	}
	
	@PostMapping
	public ResponseEntity<Object> refreshToken(HttpServletRequest request) {
		// 1. 쿠키에서 리프레시 토큰 읽기
		System.out.println("리프레시");
	    String refreshToken = null;
	    System.out.println(request.getCookies());
	    if (request.getCookies() != null) {
	        for (Cookie cookie : request.getCookies()) {
	        	System.out.println(cookie.getName());
	            if ("refreshToken".equals(cookie.getName())) {
	                refreshToken = cookie.getValue();
	                break;
	            }
	        }
	    }
	    if(refreshToken == null) {
	    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("리프레시 토큰이 없습니다.");
	    }
	    
	    // 3. 리프레시 토큰 검증 및 처리
	    if (!jwtUtils.validateToken(refreshToken)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰입니다.");
	    }
	    
	    String userEmail = jwtUtils.getUserEmail(refreshToken);
	    String userRole = jwtUtils.getRole(refreshToken);
	    String userNickname = jwtUtils.getNickname(refreshToken);
	    int userId = jwtUtils.getUserIdFromToken(refreshToken);
	    if (!refreshTokenService.validateRefreshToken(userEmail, refreshToken)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰입니다.");
	    }
	    String newAccessToken = jwtUtils.createJwt(userId, userRole, userEmail, userNickname);
	    System.out.println("newAccessToken : " + newAccessToken);
	    return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
	}
	
}
