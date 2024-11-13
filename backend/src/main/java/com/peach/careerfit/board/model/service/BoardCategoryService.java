package com.peach.careerfit.board.model.service;

import java.util.List;

import com.peach.careerfit.board.model.dto.BoardCategory;

public interface BoardCategoryService {
	int registBoardCategory(BoardCategory boardCategory);
	List<BoardCategory> getBoardCategoryAll();
}
