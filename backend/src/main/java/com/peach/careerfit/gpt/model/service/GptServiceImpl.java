package com.peach.careerfit.gpt.model.service;

import org.springframework.stereotype.Service;

import com.peach.careerfit.gpt.model.dto.Gpt;

@Service
public class GptServiceImpl implements GptService{

	@Override
	public int registMessage(Gpt gpt) {
		System.out.println("저장하는 서비스 부름");
		return 1;
	}
	
}
