package com.peach.careerfit.board.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peach.careerfit.board.model.dto.BoardCategory;
import com.peach.careerfit.board.model.service.BoardCategoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/board/category")
@RestController
public class BoardCategoryRestController {

	private final BoardCategoryService boardCategoryService;

	@PostMapping
	public ResponseEntity<Object> registBoardCategory(@RequestBody BoardCategory boardCategory) {
		int status = boardCategoryService.registBoardCategory(boardCategory);
		if (status == 0) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body("리소스가 성공적으로 생성되었습니다.");
	}

	@GetMapping
	public ResponseEntity<Object> getBoardCategory() {
		List<BoardCategory> boardCategories = boardCategoryService.getBoardCategoryAll();
		if (boardCategories.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("카테고리가 없습니다.");
		return ResponseEntity.status(HttpStatus.OK).body(boardCategories);
	}
}
