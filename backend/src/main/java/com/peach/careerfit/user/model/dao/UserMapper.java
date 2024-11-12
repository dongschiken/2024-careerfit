package com.peach.careerfit.user.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.peach.careerfit.user.model.dto.User;

@Mapper
public interface UserMapper {
	User findByUserEmail(@Param("email") String userEmail);
	void insertUser(User user);
	
	// 마이페이지 유저 정보 조회
	User findById(@Param("user_id") int userId);
	
	// 회원 정보 변경
	int updateUser(@Param("user_id") int userId, @Param("user") User user);
	
	// 프로필 변경
	int updateProfilePicture(@Param("user_id") int userId, @Param("profile_url") String profileUrl);

	// 회원 탈퇴 (status :  0)
	int updateUserStatus(@Param("user_id") int userId, @Param("status") int status);

	// 유저 비밀번호 업데이트
	int updateUserPassword(@Param("user_id") int userId, @Param("password") String password);
}
