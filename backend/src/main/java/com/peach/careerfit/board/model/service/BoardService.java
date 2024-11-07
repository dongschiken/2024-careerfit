package com.peach.careerfit.board.model.service;

import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.board.model.dto.Board;

public interface BoardService {
	int registBoard(Board board, MultipartFile[] files);
}
