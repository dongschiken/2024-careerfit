package com.peach.careerfit.board.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.peach.careerfit.board.model.dto.Board;
import com.peach.careerfit.board.model.dto.BoardImg;
import com.peach.careerfit.board.model.dto.BoardSearch;
import com.peach.careerfit.board.model.dto.ResponseBoard;

@Mapper
public interface BoardDao {
	int insertBoard(Board board);
	int insertBoardImgs(List<BoardImg> boardImgs);
	List<ResponseBoard> selectBoardAll(BoardSearch boardSearch);
	ResponseBoard selectBoardById(int boardId);
	int updateBoardDeleteWhetherById(int boardId);
	List<BoardImg> selectBoardImgbyBoardId(int boardId);
	int deleteBoardImgs(int boardId);
	int updateBoard(Board board);
	int selectBoardsCount(BoardSearch boardSearch);
	Integer getViewCount(int boardId);
	int updateViewCount(@Param("boardId") int boardId, @Param("viewCount") int viewCount);
	List<ResponseBoard> selectBoardViewRank();
	List<ResponseBoard> selectBoardReplyRank();
}
