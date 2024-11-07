package com.peach.careerfit.user.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.peach.careerfit.user.model.dto.User;

@Mapper
public interface UserMapper {
	User findByUserEmail(@Param("email") String userEmail);
	void insertUser(User user);
}
