package com.peach.careerfit.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardImg {
	private int boardImgId;
	private int boardId;
	private String path;
	private String systemName;
	private String originName;
	private String mainWhether;
}