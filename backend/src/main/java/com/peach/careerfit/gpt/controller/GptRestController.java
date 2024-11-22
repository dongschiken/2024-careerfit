package com.peach.careerfit.gpt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peach.careerfit.gpt.model.dto.GptResponse;
import com.peach.careerfit.gpt.model.service.GptService;
import com.peach.careerfit.jwt.JwtResponse;
import com.peach.careerfit.jwt.JwtUtils;
import com.peach.careerfit.user.model.dto.ResponseTokenUser;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/gpt")
public class GptRestController {

	private final JwtUtils jwtUtils;
	private final GptService gptService;
	private final JwtResponse jwtResponse;

	public GptRestController(JwtUtils jwtUtils, GptService gptService, JwtResponse jwtResponse) {
		this.jwtUtils = jwtUtils;
		this.gptService = gptService;
		this.jwtResponse = jwtResponse;
	}

	// 처음 gpt 페이지를 오픈하면 안녕하세요 ~~ 하는 gpt를 받아와야함
	@PostMapping("/first")
	public ResponseEntity<Object> initialGpt(HttpServletRequest request) {
		ResponseTokenUser user = jwtResponse.extractTokenUser(request);
		GptResponse response = gptService.initailGpt(user.getUserId());
		if(response == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("읽어올 프롬프트 파일이 없습니다.");
		}
		return ResponseEntity.ok(response);
	}
	
	
	/**
	 * 회원이면 userGpt
	 * 회원아니면 gptGpt
	 * @param request
	 * @param prompt
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Object> registMessage(HttpServletRequest request, @RequestBody String prompt) {
		ResponseTokenUser user = jwtResponse.extractTokenUser(request);
		GptResponse response = gptService.registAndRequestGpt(user.getUserId(), prompt);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
