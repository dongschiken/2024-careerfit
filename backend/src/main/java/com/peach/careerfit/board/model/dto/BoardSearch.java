package com.peach.careerfit.board.model.dto;

import lombok.Data;

@Data
public class BoardSearch {
	private static final int LIST_SIZE = 10;
	private int page;
	private int listSize;
	private String searchWord;
	private int boardCategoryId;
	private String sortOrder;
    public BoardSearch() {
        this(1, "", 0, "");
    }

    public BoardSearch(String searchWord) {
        this(1, searchWord, 0, "");
    }

    public BoardSearch(int boardCategoryId) {
        this(1, "", boardCategoryId, "");
    }
    
    public BoardSearch(int page, String searchWord) {
        this(page, searchWord, 0, "");
    }

    public BoardSearch(int page, String searchWord, Integer boardCategoryId, String sortOrder) {
        this.page = page;
        this.listSize = LIST_SIZE;
        this.searchWord = searchWord;
        this.boardCategoryId = boardCategoryId;
        this.sortOrder = sortOrder;
    }
	
	public BoardSearch(int page, int listSize) {
		this.page = page;
		this.listSize = listSize;
	}
	
	public int getOffset() {
		return (page - 1) * listSize;
	}
	
}