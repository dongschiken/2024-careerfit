package com.peach.careerfit.board.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * 페이징 처리를 위한 변수를 표현
 * 
 */
@ToString
public class PageResult {
	private static final int TAB_SIZE = 10;
	private static final int LIST_SIZE = 10;
	private int page;
	private int lastPage;  
	private int beginPage;
	private int endPage;  
	private boolean prev;  
	private boolean next; 
	
	public PageResult(int page, int totalCount) {
		this(page, totalCount, LIST_SIZE, TAB_SIZE);
	}
	
	public PageResult(int page, int totalCount, int listSize) {
		this(page, totalCount, listSize, TAB_SIZE);
	}
	
	public PageResult (int page, int totalCount, int listSize, int tabSize) {
		this.lastPage = (totalCount % listSize == 0) ? totalCount / listSize : totalCount / listSize + 1;
		int tab = (page - 1) / tabSize + 1;
		this.beginPage = (tab - 1) * tabSize + 1;
		this.endPage = (tab * tabSize < lastPage) ? tab * tabSize : lastPage ;
		this.prev = beginPage != 1; 
		this.next = endPage != lastPage; 
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	public int getLastPage() {
		return lastPage;
	}

	public int getBeginPage() {
		return beginPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public boolean isPrev() {
		return prev;
	}

	public boolean isNext() {
		return next;
	}
	
}
