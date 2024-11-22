package com.peach.careerfit.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.board.model.dto.Board;
import com.peach.careerfit.board.model.dto.BoardSearch;
import com.peach.careerfit.board.model.dto.ResponseBoard;
import com.peach.careerfit.board.model.service.BoardService;
import com.peach.careerfit.jwt.JwtResponse;
import com.peach.careerfit.jwt.JwtUtils;
import com.peach.careerfit.user.model.dto.ResponseTokenUser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/board")
public class BoardRestController {

	private final BoardService boardService;
	private final JwtUtils jwtUtils;
	private final JwtResponse jwtResponse;

	@GetMapping("/{boardId}")
	public ResponseEntity<Object> getBoardById(@PathVariable("boardId") int boardId, HttpServletRequest request) {
		ResponseTokenUser responseTokenUser = jwtResponse.extractTokenUser(request);
		ResponseBoard board = boardService.getBoardById(boardId,
				responseTokenUser != null ? responseTokenUser.getUserId() : 0);
		Map<String, Object> response = new HashMap<>();
		response.put("board", board);
		response.put("user", responseTokenUser);
		try {
			if (board == null) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("찾는 데이터가 없습니다.");
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 페이징 처리
	@GetMapping
	public ResponseEntity<Object> getBoardList(@RequestParam(required = false) String searchWord,
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "0") int categoryId,
			@RequestParam(required = false) String sortOrder) {
		BoardSearch boardSearch = new BoardSearch(page, searchWord, categoryId, sortOrder);
		Map<String, Object> response = boardService.getBoardList(boardSearch);
		try {
			if (response.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("찾는 데이터가 없습니다.");
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<Object> Registboard(@RequestPart(name = "board") Board board,
			@RequestPart(name = "files", required = false) List<MultipartFile> files, HttpServletRequest request) {

		try {
			String token = jwtUtils.getAccessToken(request);
			board.setUserId(jwtUtils.getUserIdFromToken(token));
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
			int status = boardService.setBoard(board, files);
			if (status < 1) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body("리소스가 성공적으로 수정되었습니다.");
	}

}
