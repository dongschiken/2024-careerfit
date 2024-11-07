package com.peach.careerfit.user.model.service;

import com.peach.careerfit.user.model.dto.User;

public interface UserService {
	public User findUserByEmail(String email);
	public void registUser(User user);
}
