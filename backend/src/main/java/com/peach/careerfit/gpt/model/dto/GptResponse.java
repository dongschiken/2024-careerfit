package com.peach.careerfit.gpt.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
/**
 * gpt 요청을 받을 객체
 */
public class GptResponse {
	private List<Choice> choices;
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Choice {
		private int index;
		private Message message;
	}
}
