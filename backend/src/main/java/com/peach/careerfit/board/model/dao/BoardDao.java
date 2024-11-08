package com.peach.careerfit.board.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.board.model.dto.Board;
import com.peach.careerfit.board.model.dto.BoardImg;

@Mapper
public interface BoardDao {
	int insertBoard(Board board);
	int insertBoardImg(BoardImg boardImg);
	List<Board> selectBoardAll();
	Board selectBoardById(int boardId);
}
