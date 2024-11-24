package com.peach.careerfit.gpt.model.service;

import java.util.Map;

import com.peach.careerfit.gpt.model.dto.Gpt;
import com.peach.careerfit.gpt.model.dto.GptResponse;

public interface GptService {
	GptResponse initailGpt(int userId);
	Map<String, Object> registAndRequestGpt(int userId, String message);
}
