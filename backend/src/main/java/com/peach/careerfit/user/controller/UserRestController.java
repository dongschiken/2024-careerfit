package com.peach.careerfit.user.controller;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peach.careerfit.jwt.JwtResponse;
import com.peach.careerfit.jwt.JwtUtils;
import com.peach.careerfit.user.model.dto.LoginRequest;
import com.peach.careerfit.user.model.dto.User;
import com.peach.careerfit.user.model.service.UserService;


@RestController
@RequestMapping("/api")
public class UserRestController {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;

	public UserRestController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
	}

	@PostMapping("/login")
	public ResponseEntity<Object> getUser(@RequestBody LoginRequest loginRequest) {
		System.out.println("login");
		System.out.println(loginRequest.getEmail());
		System.out.println(loginRequest.getPassword());
		// 사용자 인증 시도
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
				);
		System.out.println(authentication);
		System.out.println(11);
		User loginUser = userService.findUserByEmail(loginRequest.getEmail());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		//
		// JWT 토큰 생성
		String token = jwtUtils.createJwt(
				loginUser.getRole(),
				loginUser.getEmail(),
				loginUser.getNickname()
				);
		System.out.println("token"+token);
		// 응답으로 토큰 전달
		return ResponseEntity.ok(new JwtResponse(token));
	}
	
	@PostMapping("/regist")
	public ResponseEntity<Object> doRegist(@RequestBody User user) {
		System.out.println(user);
		LocalDate ld = LocalDate.now();
		user.setCreatedAt(ld);
		user.setUpdatedAt(ld);
		user.setStatus(1);
		userService.registUser(user);
		return ResponseEntity.ok().build();
	}
}

