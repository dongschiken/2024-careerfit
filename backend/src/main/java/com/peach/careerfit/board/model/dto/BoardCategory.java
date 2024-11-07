package com.peach.careerfit.board.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCategory {
	private int boardCategoryId;
	private String name;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
