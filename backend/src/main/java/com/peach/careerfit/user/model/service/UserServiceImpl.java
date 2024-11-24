// UserService.java
package com.peach.careerfit.user.model.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.component.FileStorageComponent;
import com.peach.careerfit.user.model.dao.UserMapper;
import com.peach.careerfit.user.model.dto.User;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender mailSender;
    private final FileStorageComponent fileStorageComponent;
    private final static String role = "ROLE_USER";
    private static final String type = "UserProfile";
    
    public UserServiceImpl(UserMapper userMapper, BCryptPasswordEncoder bCryptPasswordEncoder, JavaMailSender mailSender, FileStorageComponent fileStorageComponent) {
        this.userMapper = userMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mailSender = mailSender;
        this.fileStorageComponent = fileStorageComponent;
    }

    public User findUserByEmail(String email) {
        return userMapper.findByUserEmail(email);
    }
    
    public boolean authenticateUser(String email, String password) {
        User user = findUserByEmail(email);
        if (user == null && !bCryptPasswordEncoder.matches(password, user.getPassword())) {
        	throw new RuntimeException("잘못된 이메일 또는 비밀번호입니다.");
        }
        
        if(user.getStatus() == 0) {
        	throw new RuntimeException("탈퇴한 회원입니다. 로드인이 불가능합니다.");
        }
        // 로그인 성공
        return true;
    }

	@Override
	public void registUser(User user) {
		
	    String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
	    user.setRole(role);
	    user.setPassword(encodedPassword);
		userMapper.insertUser(user);
		System.out.println("회원 등록 완료!!!!!!!");
	}

	@Override
	public User getUserById(int userId) {
		return userMapper.findById(userId);
	}
	
	@Override
	public int updateUser(int userId, User user) {
		 // 기존 사용자 정보 조회
	    User currentUser = userMapper.findById(userId);
	    if (currentUser == null) {
	        throw new RuntimeException("사용자를 찾을 수 없습니다.");
	    }

	    // 닉네임이 변경된 경우에만 추가 처리
	    if (!currentUser.getNickname().equals(user.getNickname())) {
	        // 닉네임 중복 체크
	        User existingUser = userMapper.findByUserNickname(user.getNickname());
	        if (existingUser != null) {
	            throw new RuntimeException("이미 사용중인 닉네임입니다.");
	        }
	        
	        // 게시글, 댓글 등의 닉네임도 함께 업데이트
//	        userMapper.updateUserNicknameInBoard(userId, user.getNickname());
//	        userMapper.updateUserNicknameInReply(userId, user.getNickname());
	    }

	    return userMapper.updateUser(userId, user);
	}
	

	// 프로필 이미지 변경
	@Override
	public int updateProfilePicture(int userId, MultipartFile file) {
		String img = fileStorageComponent.saveFile(file, type);
		return userMapper.updateProfilePicture(userId, img);
	}

	// 회원 탈퇴 (status : 0)
	@Override
	public boolean deactivateUser(int userId) {
		int result = userMapper.updateUserStatus(userId, 0);
		return result > 0;
	}

	// 현재 비밀번호 확인
	@Override
	public boolean checkCurrentPassword(int userId, String currentPassword) {
		User user = userMapper.findById(userId);	
		if(user == null || !bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
			return false;			
		}
		return bCryptPasswordEncoder.matches(currentPassword, user.getPassword());	// 현재 비밀번호가 일치함
	}

	// 새 비밀번호 업데이트
	@Override
	public boolean changePassword(int userId, String newPassword) {
		String encodedNewPassword = bCryptPasswordEncoder.encode(newPassword);
		int result = userMapper.updateUserPassword(userId, encodedNewPassword);
		return result > 0;		// 업데이트 성공 여부 반환
	}

	// 새 비밀번호 복잡성 검사 로직 추가
	@Override
	public boolean isPasswordComplexEnough(String password) {
		if(password.length() < 8) return false;		// 최소 길이 8자 이상
		
		boolean hasUppercase = false;
		boolean hasLowercase = false;
		boolean hasDigit = false;
		boolean hasSpecialChar = false;
		
		for(char c : password.toCharArray()) {
			if(Character.isUpperCase(c)) hasUppercase = true;
			else if(Character.isLowerCase(c)) hasLowercase = true;
			else if(Character.isDigit(c)) hasDigit = true;
			else hasSpecialChar = true;
			
			// 모든 조건 만족 시 true 반환
			if(hasUppercase && hasLowercase && hasDigit && hasSpecialChar) return true;
		}
		return false;
	}

	// 비밀번호 변경 알림 이메일 전송 로직 추가
	@Override
	public void sendPasswordChangeEmail(int userId) {
		User user = userMapper.findById(userId);
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("비밀번호 변경 알림");
		message.setText("안녕하세요" + user.getNickname() + "님,\n\n 귀하의 계정의 비밀번호가 성공적으로 변경되었습니다. \n\n감사합니다.");
		
		mailSender.send(message);
	}

	@Override
	public User findUserByNickname(String nickname) {
		return userMapper.findByUserNickname(nickname);
	}

	@Override
	public boolean isEmailAvailable(String email) {
		return userMapper.countByEmail(email) == 0;
	}


}
