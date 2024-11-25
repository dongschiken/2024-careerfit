package com.peach.careerfit.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.board.model.dto.Board;
import com.peach.careerfit.board.model.dto.BoardSearch;
import com.peach.careerfit.board.model.dto.ResponseBoard;

public interface BoardService {
	int registBoard(Board board, List<MultipartFile> files);
	Map<String, Object> getBoardList(BoardSearch boardSearch);
	ResponseBoard getBoardById(int boardId, int userId);
	int setBoardDeleteStatus(int boardId);
	int setBoard(Board board, List<MultipartFile> files);
	List<ResponseBoard> getBoardViewRank();
	List<ResponseBoard> getBoardReplyRank();
}
