package com.peach.careerfit.board.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.board.model.dto.BoardCategory;

@Mapper
public interface BoardCategoryDao {
	int insertBoardCategory(BoardCategory boardCategory);
	List<BoardCategory> selectBoardCategoryAll();
}
