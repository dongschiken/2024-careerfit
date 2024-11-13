package com.peach.careerfit.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardSearch {
	private int page;
	private int listSize;
	private String searchWord;
	private int boardCategoryId;
	
    public BoardSearch() {
        this(1, 10, null, null);
    }

    public BoardSearch(String searchWord) {
        this(1, 10, searchWord, null);
    }

    public BoardSearch(int boardCategoryId) {
        this(1, 10, null, boardCategoryId);
    }
    
    public BoardSearch(int page, String searchWord) {
        this(page, 10, searchWord, null);
    }

    public BoardSearch(int page, int listSize, String searchWord, Integer boardCategoryId) {
        this.page = page;
        this.listSize = listSize;
        this.searchWord = searchWord;
        this.boardCategoryId = boardCategoryId;
    }
	
	public BoardSearch(int page, int listSize) {
		this.page = page;
		this.listSize = listSize;
	}
	
	public int getOffset() {
		return (page - 1) * listSize;
	}
	
}