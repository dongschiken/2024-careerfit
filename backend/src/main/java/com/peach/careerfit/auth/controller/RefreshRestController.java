package com.peach.careerfit.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peach.careerfit.auth.model.dto.TokenRequest;
import com.peach.careerfit.auth.model.service.RefreshTokenService;
import com.peach.careerfit.jwt.JwtUtils;

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
	public ResponseEntity<Object> refreshToken(@RequestBody TokenRequest tokenRequest) {
	    String refreshToken = tokenRequest.getRefreshToken();
	    System.out.println(refreshToken);
	    String userEmail = jwtUtils.getUserEmail(refreshToken);
	    String userRole = jwtUtils.getRole(refreshToken);
	    String userNickname = jwtUtils.getNickname(refreshToken);
	    int userId = jwtUtils.getUserIdFromToken(refreshToken);
	    if (!refreshTokenService.validateRefreshToken(userEmail, refreshToken)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰입니다.");
	    }
	    String newAccessToken = jwtUtils.createJwt(userId, userRole, userEmail, userNickname);
	    System.out.println("엑세스 토큰"+newAccessToken);
	    return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
	}
	
}
