package com.peach.careerfit.board.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.board.model.dto.Board;

public interface BoardService {
	int registBoard(Board board, String email, MultipartFile[] files);
	List<Board> getBoardList();
	Board getBoardById(int boardId);
	int setBoardDeleteStatus(int boardId);
}
