package com.peach.careerfit.board.model.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.board.model.dao.BoardDao;
import com.peach.careerfit.board.model.dto.Board;
import com.peach.careerfit.board.model.dto.BoardImg;
import com.peach.careerfit.file.component.FileStorageComponent;

@Service
public class BoardServiceImpl implements BoardService {
	
    private final BoardDao boardDao;
    private final FileStorageComponent fileStorageComponent;
    private static final String type = "Board";
    public BoardServiceImpl(BoardDao boardDao, FileStorageComponent fileStorageComponent) {
        this.boardDao = boardDao;
        this.fileStorageComponent = fileStorageComponent;
    }

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
        if(!boardImgs.isEmpty()) boardDao.insertBoardImgs(boardImgs);
		System.out.println(boardImgs);
        return status;
    }

	@Override
	public List<Board> getBoardList() {
		return boardDao.selectBoardAll();
	}

	@Override
	public Board getBoardById(int boardId) {
		Board board = boardDao.selectBoardById(boardId);
		List<BoardImg> boardImgs = boardDao.selectBoardImgbyBoardId(boardId);
		board.setBoardImgs(boardImgs);
		return board;
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
		boardDao.insertBoardImgs(boardImgs);
		return status;
	}

}
