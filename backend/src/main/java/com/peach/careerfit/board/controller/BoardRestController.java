package com.peach.careerfit.board.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.board.model.dto.Board;
import com.peach.careerfit.board.model.dto.BoardCategory;
import com.peach.careerfit.board.model.service.BoardCategoryService;
import com.peach.careerfit.board.model.service.BoardService;
import com.peach.careerfit.jwt.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/board")
public class BoardRestController {

	private final BoardService boardService;
	private final BoardCategoryService boardCategoryService;
	private final JwtUtils jwtUtils;

	public BoardRestController(BoardService boardService, BoardCategoryService boardCategoryService,
			JwtUtils jwtUtils) {
		this.boardService = boardService;
		this.boardCategoryService = boardCategoryService;
		this.jwtUtils = jwtUtils;
	}
	
	@GetMapping("/{boardId}")
	public ResponseEntity<Object> getBoardById(@PathVariable("boardId") int boardId) {
		Board board = boardService.getBoardById(boardId);
		try {
			if (board == null) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("찾는 데이터가 없습니다.");
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(board);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 페이징 처리
	@GetMapping
	public ResponseEntity<Object> getBoardList() {
		List<Board> boards = boardService.getBoardList();
		try {
			if (boards.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("찾는 데이터가 없습니다.");
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(boards);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping
	public ResponseEntity<Object> Registboard(@RequestPart(value = "board") Board board,
											  @RequestPart(value = "files", required = false) List<MultipartFile> files,
											  HttpServletRequest request) {
		try {
			String token = jwtUtils.getAccessToken(request);
			board.setUserId(jwtUtils.getUserIdFromToken(token));
			System.out.println(files.size());
			System.out.println(files);
			int status = boardService.registBoard(board, files);
			if (status < 1) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body("리소스가 성공적으로 생성되었습니다.");
	}

	@PostMapping("/category")
	public ResponseEntity<Object> registBoardCategory(@RequestBody BoardCategory boardCategory) {
		int status = boardCategoryService.registBoardCategory(boardCategory);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body("리소스가 성공적으로 생성되었습니다.");
	}

	@PutMapping("/{boardId}")
	public ResponseEntity<Object> setBoardDeleteStatus(@PathVariable("boardId") int boardId) {
		int status = boardService.setBoardDeleteStatus(boardId);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("게시글이 성공적으로 삭제되었습니다.");
	}

	@PutMapping
	public ResponseEntity<Object> setboard(@RequestPart(value = "board") Board board,
										   @RequestPart(value = "files", required = false) List<MultipartFile> files,
											  HttpServletRequest request) {
		try {
			String token = jwtUtils.getAccessToken(request);
			board.setUserId(jwtUtils.getUserIdFromToken(token));
			int status = boardService.setBoard(board, files);
			if (status < 1) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body("리소스가 성공적으로 생성되었습니다.");
	}

}
