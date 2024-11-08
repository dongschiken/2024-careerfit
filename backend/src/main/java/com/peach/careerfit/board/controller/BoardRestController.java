package com.peach.careerfit.board.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.board.model.dto.Board;
import com.peach.careerfit.board.model.dto.BoardCategory;
import com.peach.careerfit.board.model.service.BoardCategoryService;
import com.peach.careerfit.board.model.service.BoardService;
import com.peach.careerfit.jwt.JwtUtils;
import com.peach.careerfit.user.model.dto.CustomUserDetails;

@RestController
@RequestMapping("/api/board")
public class BoardRestController {

	private final BoardService boardService;
	private final BoardCategoryService boardCategoryService;
	private final JwtUtils jwtUtils;
	public BoardRestController(BoardService boardService
			, BoardCategoryService boardCategoryService
			, JwtUtils jwtUtils) { 
		this.boardService = boardService;
		this.boardCategoryService = boardCategoryService;
		this.jwtUtils = jwtUtils;
	}
	
	@GetMapping("/{boardId}")
	public ResponseEntity<Object> getBoardById(@PathVariable("boardId") int boardId){
		Board board = boardService.getBoardById(boardId);
		try {
			if(board == null) {
				return ResponseEntity
						.status(HttpStatus.NO_CONTENT)
						.body("찾는 데이터가 없습니다.");
			}else {
				return ResponseEntity.status(HttpStatus.OK).body(board);
			}
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.build();
		}
	}
	
	@GetMapping
	public ResponseEntity<Object> getBoardList(){
		List<Board> boards = boardService.getBoardList();
		try {
			if(boards.isEmpty()) {
				return ResponseEntity
						.status(HttpStatus.NO_CONTENT)
						.body("찾는 데이터가 없습니다.");
			}else {
				return ResponseEntity.status(HttpStatus.OK).body(boards);
			}
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.build();
		}
	}
	
	@PostMapping
	public ResponseEntity<Object> Registboard(
			@RequestBody Board board
			/*@RequestParam MultipartFile[] files*/) {
		MultipartFile[] files = null;
	    // SecurityContext에서 현재 인증된 사용자 정보 가져오기
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	    
	    // userEmail을 사용
	    String userEmail = userDetails.getUsername();
	    String role = userDetails.getAuthorities().toString(); // 역할 사용
		int status = boardService.registBoard(board, userEmail, files);
		if(status == 0) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.build();
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body("리소스가 성공적으로 생성되었습니다.");
	}

	@PostMapping("/category")
	public ResponseEntity<Object> RegistboardCategory(@RequestBody BoardCategory boardCategory){
		int status = boardCategoryService.registBoardCategory(boardCategory);
		if(status == 0) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.build();
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body("리소스가 성공적으로 생성되었습니다.");
	}
}
