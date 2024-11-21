package com.peach.careerfit.jwt;


import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.peach.careerfit.user.model.dto.CustomUserDetails;
import com.peach.careerfit.user.model.dto.User;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 요청에 대해서 1번 동작하는 OncePerRequestFilter 상속받는다.
 */
public class JwtFilter extends OncePerRequestFilter {

	private JwtUtils jwtUtils;
	public JwtFilter(JwtUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		// Authorization 헤더에서 JWT 토큰을 추출
		String token = jwtUtils.getAccessToken(request);
		try {
			if (token != null && jwtUtils.validateToken(token)) {
				// 토큰이 유효한지 검사
				// JWT가 유효한 경우, 토큰에서 사용자 정보를 추출
				String userEmail = jwtUtils.getUserEmail(token);
				String role = jwtUtils.getRole(token);
				// 사용자 정보를 CustomUserDetails 객체로 설정
				User user = new User();
				user.setEmail(userEmail);
				user.setRole(role); // 사용자 역할 설정 (예: ROLE_USER)
				CustomUserDetails customUserDetails = new CustomUserDetails(user);

				// Authentication 객체를 생성하여 SecurityContext에 설정
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			// 다음 필터로 요청을 전달
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 반환
			response.getWriter().write("{\"error\": \"Access token expired\"}");
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 반환
			response.getWriter().write("{\"error\": \"Invalid token\"}");
		}
	}
}
