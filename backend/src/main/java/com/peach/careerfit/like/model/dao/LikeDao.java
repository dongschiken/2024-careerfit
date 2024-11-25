package com.peach.careerfit.like.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.board.model.dto.Board;

@Mapper
public interface LikeDao {
	int insertLike(Board board);
}
