package com.peach.careerfit.board.model.service;

import org.springframework.stereotype.Service;

import com.peach.careerfit.board.model.dao.BoardCategoryDao;
import com.peach.careerfit.board.model.dto.BoardCategory;

@Service
public class BoardCategoryServiceImpl implements BoardCategoryService{

	private final BoardCategoryDao boardCategoryDao;
	public BoardCategoryServiceImpl(BoardCategoryDao boardCategoryDao) {
		this.boardCategoryDao = boardCategoryDao;
	}
	
	@Override
	public int registBoardCategory(BoardCategory boardCategory) {
		return boardCategoryDao.insertBoardCategory(boardCategory);
	}
	
}
