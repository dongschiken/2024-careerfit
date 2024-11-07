package com.peach.careerfit.board.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
	private int board_id;	
	private int user_id;
	private int category_id;
	private String title;
	private String content;
	private int like_count;
	private int view_count;
	private LocalDateTime created_at;
	private LocalDateTime updated_at;	
	private String address;
	private List<BoardImg> boardImgs;
}
