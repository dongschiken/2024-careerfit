package com.peach.careerfit.gpt.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gpt {
	private Long id;
	private int userId;
	private String context;
	private String isUserChat;
	private LocalDateTime createdAt;
}
