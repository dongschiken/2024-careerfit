package com.peach.careerfit.user.model.service;

import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.user.model.dto.User;

public interface UserService {
	public User findUserByEmail(String email);
	public void registUser(User user);
	
	// 마이페이지 조회
	public User getUserById(int userId);
	
	// 회원정보 수정
	int updateUser(int userId, User user);
	
	// 프로필 변경
	int updateProfilePicture(int userId, MultipartFile file);

	// 회원 탈퇴
	boolean deactivateUser(int userId);
	
	// 비밀번호 변경
	boolean checkCurrentPassword(int userId, String currentPassword);
	boolean changePassword(int userId, String newPassword);
	boolean isPasswordComplexEnough(String password);
	void sendPasswordChangeEmail(int userId);
}
