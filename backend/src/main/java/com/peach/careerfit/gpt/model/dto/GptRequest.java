package com.peach.careerfit.gpt.model.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * GPT에 요청을 보낼 객체
 */
public class GptRequest {
	private String model;
	private List<Message> messages;
	private double temperature;
	
	
	public GptRequest(String model, String prompt, String role) {
		this.model = model;
		this.messages = new ArrayList<>();
		this.messages.add(new Message(role, prompt));
	}
}
