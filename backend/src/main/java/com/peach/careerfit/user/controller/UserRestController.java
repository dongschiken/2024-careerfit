package com.peach.careerfit.user.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.jwt.JwtResponse;
import com.peach.careerfit.jwt.JwtUtils;
import com.peach.careerfit.user.model.dto.LoginRequest;
import com.peach.careerfit.user.model.dto.PasswordChangeRequest;
import com.peach.careerfit.user.model.dto.User;
import com.peach.careerfit.user.model.service.UserService;

@RestController
@RequestMapping("/api")
public class UserRestController {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;

	public UserRestController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
	}
	
	// 로그인
	@PostMapping("/login")
	public ResponseEntity<Object> getUser(@RequestBody LoginRequest loginRequest) {
		System.out.println("login");
		System.out.println(loginRequest.getEmail());
		System.out.println(loginRequest.getPassword());
		try {

			// 사용자 인증 시도
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
			System.out.println(authentication);
			System.out.println(11);
			User loginUser = userService.findUserByEmail(loginRequest.getEmail());

			// 탈퇴한 회원인지 확인
			if (loginUser.getStatus() == 0) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("탈퇴한 회원입니다. 로그인이 불가능합니다.");
			}

			SecurityContextHolder.getContext().setAuthentication(authentication);
			//
			// JWT 토큰 생성
			String token = jwtUtils.createJwt(loginUser.getUserId(), loginUser.getRole(), loginUser.getEmail(),
					loginUser.getNickname());
			System.out.println("token" + token);
			// 응답으로 토큰 전달
			return ResponseEntity.status(HttpStatus.OK).body(new JwtResponse(token));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 이메일 또는 비밀번호입니다.");
		}
	}

	// 회원가입
	@PostMapping("/join")
	public ResponseEntity<Object> doRegist(@RequestBody User user) {
		System.out.println(user);
		LocalDate ld = LocalDate.now();
		user.setCreatedAt(ld);
		user.setUpdatedAt(ld);
		user.setStatus(1);
		userService.registUser(user);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	// 마이페이지 조회
	@GetMapping("/user/{user_id}")
	public ResponseEntity<User> getUserById(@PathVariable("user_id") int userId) {
		User user = userService.getUserById(userId);
		if (user != null) {
			return ResponseEntity.status(HttpStatus.OK).body(user);
		} else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}

	// 회원정보 수정
	@PutMapping("/user/{user_id}")
	public ResponseEntity<String> updateUser(@PathVariable("user_id") int userId, @RequestBody User user) {
		System.out.println("###");
		try {
			int result = userService.updateUser(userId, user);
			if (result > 0) {
				return ResponseEntity.status(HttpStatus.OK).body("회원 정보가 성공적으로 수정되었습니다.");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 정보 수정에 실패했습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("처리중 오류가 발생했습니다.");
		}
	}

	// 프로필 이미지 변경
	@PutMapping("/user/{user_id}/profile-picture")
	public ResponseEntity<String> updateProfilePicture(@PathVariable("user_id") int userId,
			@RequestParam("file") MultipartFile file) {
		try {
			int result = userService.updateProfilePicture(userId, file);
			if (result > 0) {
				return ResponseEntity.status(HttpStatus.OK).body("프로필 이미지가 성공적으로 변경되었습니다.");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 이미지 변경에 실패했습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("처리중 오류가 발생했습니다.");
		}
	}

	// 회원 탈퇴 (status : 0)
	@PutMapping("/user/{user_id}/deactivate")
	public ResponseEntity<String> deactivateUser(@PathVariable("user_id") int userId) {
		try {
			boolean isDeactivated = userService.deactivateUser(userId);
			if (isDeactivated) {
				return ResponseEntity.status(HttpStatus.OK).body("회원이 성공적으로 탈퇴 처리되었습니다.");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 탈퇴 처리 중 오류가 발생했습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}

	// 비밀번호 변경
	@PutMapping("/user/{user_id}/password")
	public ResponseEntity<String> changePassword(@PathVariable("user_id") int userId,
			@RequestBody PasswordChangeRequest request) {
		try {
			// 현재 비밀번호가 일치하는지 확인
			boolean isCurrentPasswordValid = userService.checkCurrentPassword(userId, request.getCurrentPassword());
			if (!isCurrentPasswordValid) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("현재 비밀번호가 일치하지 않습니다.");
			}
			// 새 비밀번호와 확인 비밀번호가 일치하는지 확인
			if (!request.getNewPassword().equals(request.getConfirmPassword())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 서로 일치하지 않습니다.");
			}

			// 새 비밀번호 복합성 검사
			if (!userService.isPasswordComplexEnough(request.getNewPassword())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("비밀번호는 최소 8자 이상, 대문자, 소문자, 숫자 및 특수 문자를 포함해야 합니다.");
			}

			// 비밀번호 변경 처리
			boolean isPasswordChanged = userService.changePassword(userId, request.getNewPassword());
			if (isPasswordChanged) {
				// 비밀번호 변경 알림 이메일 전송
				userService.sendPasswordChangeEmail(userId);
				return ResponseEntity.status(HttpStatus.OK).body("비밀번호가 성공적으로 변경되었습니다.");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 중 오류가 발생했습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}
}
