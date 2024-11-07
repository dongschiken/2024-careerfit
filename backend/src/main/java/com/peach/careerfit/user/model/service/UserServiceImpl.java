// UserService.java
package com.peach.careerfit.user.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.peach.careerfit.user.model.dao.UserMapper;
import com.peach.careerfit.user.model.dto.User;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    
    public UserServiceImpl(UserMapper userMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userMapper = userMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByEmail(String email) {
        return userMapper.findByUserEmail(email);
    }
    
    public boolean authenticateUser(String email, String password) {
        User user = findUserByEmail(email);
        if (user != null && bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return true;
        }
        return false;
    }

	@Override
	public void registUser(User user) {
	    String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
	    user.setPassword(encodedPassword);
		userMapper.insertUser(user);
	}
}
