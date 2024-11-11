package com.peach.careerfit.user.model.service;

import com.peach.careerfit.user.model.dto.User;

public interface UserService {
	public User findUserByEmail(String email);
	public void registUser(User user);
	
	// 마이페이지 조회
	public User getUserById(int userId);
	
	// 회원정보 수정
	int updateUser(int userId, User user);
	
	// 프로필 변경
	int updateProfilePicture(int userId, String profileUrl);
}
