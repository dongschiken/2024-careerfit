package com.peach.careerfit.board.model.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peach.careerfit.board.model.dao.BoardCategoryDao;
import com.peach.careerfit.board.model.dto.BoardCategory;

@Service
public class BoardCategoryServiceImpl implements BoardCategoryService{

	private final BoardCategoryDao boardCategoryDao;
	public BoardCategoryServiceImpl(BoardCategoryDao boardCategoryDao) {
		this.boardCategoryDao = boardCategoryDao;
	}
	
	@Transactional
	@Override
	public int registBoardCategory(BoardCategory boardCategory) {
		boardCategory.setCreatedAt(LocalDateTime.now());
		boardCategory.setUpdatedAt(LocalDateTime.now());
		return boardCategoryDao.insertBoardCategory(boardCategory);
	}

	@Override
	public List<BoardCategory> getBoardCategoryAll() {
		return boardCategoryDao.selectBoardCategoryAll();
	}
	
}
