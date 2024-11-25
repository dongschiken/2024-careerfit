package com.peach.careerfit.user.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.auth.model.dto.TokenRequest;
import com.peach.careerfit.auth.model.service.RefreshTokenService;
import com.peach.careerfit.jwt.JwtResponse;
import com.peach.careerfit.jwt.JwtUtils;
import com.peach.careerfit.user.model.dto.PasswordChangeRequest;
import com.peach.careerfit.user.model.dto.ResponseTokenUser;
import com.peach.careerfit.user.model.dto.User;
import com.peach.careerfit.user.model.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class UserRestController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final JwtResponse jwtResponse;
    private final RestTemplate restTemplate;

    public UserRestController(UserService userService, RefreshTokenService refreshTokenService, JwtUtils jwtUtils, JwtResponse jwtResponse, RestTemplate restTemplate) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtils = jwtUtils;
        this.jwtResponse = jwtResponse;
        this.restTemplate = restTemplate;
    }

    // 로그아웃
    @DeleteMapping("/logout")
    public ResponseEntity<Object> doLogout(@RequestBody TokenRequest tokenRequest) {
        String accessToken = tokenRequest.getAccessToken();
        String refreshToken = tokenRequest.getRefreshToken();
        System.out.println(accessToken);
        System.out.println(refreshToken);
        // Access Token이 비어 있는 경우
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access Token is missing");
        }

        // Access Token에서 Bearer 제거
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        try {
            if (jwtUtils.isKakaoToken(accessToken)) {
            	System.out.println("카카오 로그아웃");
                // 카카오 로그아웃 처리
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + accessToken);

                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
                restTemplate.postForEntity("https://kapi.kakao.com/v1/user/logout", requestEntity, String.class);

                return ResponseEntity.ok("Kakao logout successful");
            } else {
            	System.out.println("웹 로그아웃");
                // 일반 JWT 로그아웃 처리
                if (refreshToken == null || refreshToken.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh Token is missing");
                }

                // Refresh Token에서 사용자 이메일 추출
                String email = jwtUtils.getUserEmail(refreshToken);
                if (email == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
                }

                // Refresh Token 삭제
                refreshTokenService.deleteRefreshToken(email);
                return ResponseEntity.ok("JWT logout successful");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed");
        }
    }

	
	// 회원가입
	@PostMapping("/join")
	public ResponseEntity<Object> doRegist(@RequestBody User user) {
		System.out.println("user : "+user);
		LocalDate ld = LocalDate.now();
		user.setCreatedAt(ld);
		user.setUpdatedAt(ld);
		user.setStatus(1);
		userService.registUser(user);
		System.out.println("회원 등록 완료!!!");
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
						.body("비밀번호는 최소 8자 이상 영문, 숫자 및 특수 문자를 포함해야 합니다.");
			}

			// 비밀번호 변경 처리
		    boolean isPasswordChanged = userService.changePassword(userId, request.getNewPassword());
		    if (isPasswordChanged) {
		        // 비밀번호 변경 알림 이메일 전송 
		        userService.sendPasswordChangeEmail(userId);
		        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다."); // status code 200으로 응답
		    } else {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 중 오류가 발생했습니다.");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
		}
	}
	
	// 현재 비밀번호 확인 로직
	@PostMapping("/user/{user_id}/check-current-password")
	public ResponseEntity<Boolean> checkCurrentPassword(
	    @PathVariable("user_id") int userId,
	    @RequestBody Map<String, String> password) {
	    boolean isValid = userService.checkCurrentPassword(userId, password.get("currentPassword"));
	    return ResponseEntity.ok(isValid);
	}
	
	@GetMapping("/check-nickname")
	public ResponseEntity<Map<String, Boolean>> checkNicknameDuplicate(@RequestParam("nickname") String nickname) {
		User user = userService.findUserByNickname(nickname);
		boolean isAvailable = (user == null);
		return ResponseEntity.ok(Collections.singletonMap("available", isAvailable));
	}
	
	@GetMapping("/token-user")
	public ResponseEntity<Object> getTokenUser(HttpServletRequest request) {
		String accessToken = jwtUtils.getAccessToken(request);
		if(accessToken == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원이 없습니다.");
		}
		ResponseTokenUser responseTokenUser = new ResponseTokenUser(
				jwtUtils.getUserIdFromToken(accessToken)
				, jwtUtils.getRole(accessToken)
				, jwtUtils.getUserEmail(accessToken)
				, jwtUtils.getNickname(accessToken));
		return ResponseEntity.status(HttpStatus.OK).body(responseTokenUser);
	}
	
	@GetMapping("/user/token-user")
	public ResponseEntity<Object> getUser(HttpServletRequest request) {
		ResponseTokenUser user = jwtResponse.extractTokenUser(request);
		return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(user.getUserId()));
	}
	// UserRestController.java

	@GetMapping("/user/{user_id}/profile")
	public ResponseEntity<Map<String, String>> getUserProfile(@PathVariable("user_id") int userId) {
	    try {
	        User user = userService.getUserById(userId);
	        if (user == null) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(Collections.singletonMap("error", "User not found"));
	        }

	        // 필요한 데이터만 반환
	        Map<String, String> userProfile = Map.of(
	                "nickname", user.getNickname(),
	                "profile", user.getProfileUrl() != null ? user.getProfileUrl() : "/img/default-profile.png"
	        );
	        return ResponseEntity.ok(userProfile);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Failed to retrieve user profile"));
	    }
	}
	
	@GetMapping("/user/current")
	public ResponseEntity<User> getCurrentUser(HttpServletRequest request) {
	    // JWT 토큰에서 사용자 정보 추출
	    String token = jwtUtils.getAccessToken(request);
	    if (token == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    // 토큰에서 userId 추출
	    int userId = jwtUtils.getUserIdFromToken(token);

	    // userId로 사용자 정보 조회
	    User user = userService.getUserById(userId);

	    // 사용자 정보가 없으면 NOT_FOUND 반환
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }

	    // 사용자 정보 반환
	    return ResponseEntity.ok(user);
	}


}
