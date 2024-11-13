package com.peach.careerfit.board.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.peach.careerfit.user.model.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBoard {
	private int boardId;
	private User user;
	private BoardCategory category;
	private String title;
	private String content;
	private int likeCount;
	private int viewCount;
	private LocalDateTime createdAt;
	private String timeAgo;
	private LocalDateTime updatedAt;
	private String address;
	private String deleteWhether;
	private List<BoardImg> boardImgs;
}