package com.peach.careerfit.board.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.board.model.dto.Board;
import com.peach.careerfit.board.model.dto.BoardImg;
import com.peach.careerfit.board.model.dto.BoardSearch;

@Mapper
public interface BoardDao {
	int insertBoard(Board board);
	int insertBoardImgs(List<BoardImg> boardImgs);
	List<Board> selectBoardAll(BoardSearch boardSearch);
	Board selectBoardById(int boardId);
	int updateBoardDeleteWhetherById(int boardId);
	List<BoardImg> selectBoardImgbyBoardId(int boardId);
	int deleteBoardImgs(int boardId);
	int updateBoard(Board board);
}
