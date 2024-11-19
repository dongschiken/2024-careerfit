package com.peach.careerfit.gpt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peach.careerfit.gpt.model.dto.Gpt;

@RestController
@RequestMapping("/api/gpt")
public class GptController {
	
	@PostMapping
	public ResponseEntity<Object> registChat(@RequestBody Gpt gpt) {
		System.out.println(gpt.getMessage());
		return ResponseEntity.ok("gpt의 응답");
	}
}
