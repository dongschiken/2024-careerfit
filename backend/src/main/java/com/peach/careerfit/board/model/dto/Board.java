package com.peach.careerfit.board.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.peach.careerfit.board.model.service.BoardService;
import com.peach.careerfit.user.model.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
	private int boardId;
	private int userId;
	private int categoryId;
	private String title;
	private String content;
	private int likeCount;
	private int viewCount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;	
	private String address;
	private String deleteWhether;
	private List<BoardImg> boardImgs;
	private BoardSearch boardSearch;
}