//package com.peach.careerfit.board.controller;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.peach.careerfit.board.model.dto.Board;
//import com.peach.careerfit.board.model.dto.BoardCategory;
//import com.peach.careerfit.board.model.service.BoardService;
//
//@RestController
//@RequestMapping("/api/board")
//public class BoardRestController {
//	
//	private final BoardService boardService;
//	public BoardRestController(BoardService boardService) { 
//		this.boardService = boardService;
//	}
//	
//	@PostMapping
//	public ResponseEntity<Object> boardRegist(@RequestBody Board board) {
//		boardService.registBoard(board);
//	}
//	
//	@PostMapping("/category")
//	public ResponseEntity<Object> boardCategoryRegist(@RequestBody BoardCategory boardCategory){
//		
//	}
//}
