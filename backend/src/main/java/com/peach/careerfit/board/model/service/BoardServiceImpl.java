package com.peach.careerfit.board.model.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.board.model.dao.BoardDao;
import com.peach.careerfit.board.model.dto.Board;
import com.peach.careerfit.board.model.dto.BoardImg;
import com.peach.careerfit.board.model.dto.BoardSearch;
import com.peach.careerfit.board.model.dto.PageResult;
import com.peach.careerfit.board.model.dto.ResponseBoard;
import com.peach.careerfit.component.FileStorageComponent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

	private final BoardDao boardDao;
	private final FileStorageComponent fileStorageComponent;
	private final ViewCountService viewCountService;
	private static final String type = "Board";
	
	/**
	 * 게시글 1개 생성 시 생성날짜와 최근업데이트 날짜를 동일하게 설정하고, 파일을 업로드.
	 */
	@Transactional
	@Override
	public int registBoard(Board board, List<MultipartFile> files) {
		board.setCreatedAt(LocalDateTime.now());
		board.setUpdatedAt(LocalDateTime.now());
		int status = boardDao.insertBoard(board);
		List<BoardImg> boardImgs = fileStorageComponent.saveFiles(files, board.getBoardId(), BoardImg.class, type);
		if (!boardImgs.isEmpty())
			boardDao.insertBoardImgs(boardImgs);
		return status;
	}

	@Transactional
	@Override
	public Map<String, Object> getBoardList(BoardSearch boardSearch) {
		Map<String, Object> result = new HashMap<>();
		if(boardSearch.getSortOrder() != null && boardSearch.getSortOrder().equals("조회순")) {
			viewCountService.syncViewCountsToDatabase();
		}
		List<ResponseBoard> boards = boardDao.selectBoardAll(boardSearch);
		for (ResponseBoard board : boards) {
			board.setViewCount(viewCountService.getViewCount(board.getBoardId()));
		}
		result.put("boards", boards);
		result.put("pageResult", new PageResult(boardSearch.getPage(), boardDao.selectBoardsCount(boardSearch),
				boardSearch.getListSize()));
		result.put("boardSearch", boardSearch);
		return result;
	}
	
	@Transactional
	@Override
	public ResponseBoard getBoardById(int boardId, int userId) {
		viewCountService.incrementViewCount(boardId, userId);
		ResponseBoard responsBoard = boardDao.selectBoardById(boardId);
		System.out.println(responsBoard);
		responsBoard.setViewCount(viewCountService.getViewCount(responsBoard.getBoardId()));
		List<BoardImg> boardImgs = boardDao.selectBoardImgbyBoardId(boardId);
		responsBoard.setBoardImgs(boardImgs);
		return responsBoard;
	}

	@Override
	public int setBoardDeleteStatus(int boardId) {
		return boardDao.updateBoardDeleteWhetherById(boardId);
	}

	/**
	 * 기존 이미지를 모두 삭제하고 새로운 이미지를 insert하는 형식
	 */
	@Transactional
	@Override
	public int setBoard(Board board, List<MultipartFile> files) {
		List<BoardImg> boardImgs = fileStorageComponent.saveFiles(files, board.getBoardId(), BoardImg.class, type);
		int status = boardDao.updateBoard(board);
		boardDao.deleteBoardImgs(board.getBoardId());
		if (!boardImgs.isEmpty())
			boardDao.insertBoardImgs(boardImgs);
		return status;
	}

	@Override
	public List<ResponseBoard> getBoardViewRank() {
		List<ResponseBoard> boards =  boardDao.selectBoardViewRank();
		for (ResponseBoard board : boards) {
			board.setViewCount(viewCountService.getViewCount(board.getBoardId()));
		}
		return boards;
	}
	
	@Override
	public List<ResponseBoard> getBoardReplyRank() {
		List<ResponseBoard> boards =  boardDao.selectBoardReplyRank();
		for (ResponseBoard board : boards) {
			board.setViewCount(viewCountService.getViewCount(board.getBoardId()));
		}
		return boards;
	}
	
}
