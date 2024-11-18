package com.peach.careerfit.jwt;

import org.springframework.stereotype.Component;

import com.peach.careerfit.user.model.dto.ResponseTokenUser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtResponse {
	
	private final JwtUtils jwtUtils;
	
	/**
	 * 토큰을 받아서 회원 정보를 넘기는 메서드
	 * @param token
	 * @return ResponseTokenUser
	 */
    public ResponseTokenUser getTokenUser(String token) {
    	ResponseTokenUser responseTokenUser = new ResponseTokenUser(
    			jwtUtils.getUserIdFromToken(token),
    			jwtUtils.getUserEmail(token),
    			jwtUtils.getRole(token),
    			jwtUtils.getNickname(token)
    			);
    	return responseTokenUser;
    }
    
    /**
     * HttpServletRequest에서 토큰을 추출하고 회원 정보를 가져오는 메서드
     * @param request
     * @return ResponseTokenUser
     */
    public ResponseTokenUser extractTokenUser(HttpServletRequest request) {
        String token = jwtUtils.getAccessToken(request);
        if (token != null) {
            return getTokenUser(token);
        }
        return null;
    }
}