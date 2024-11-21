package com.peach.careerfit.gpt.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.peach.careerfit.gpt.model.dto.Gpt;

@Mapper
public interface GptDao {

	public int insertMessage(Gpt userGpt);

}
