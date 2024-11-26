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
	   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	            throws ServletException, IOException {
	        String token = jwtUtils.getAccessToken(request);
	        try {
	            if (token != null && !jwtUtils.isKakaoToken(token) && jwtUtils.validateToken(token)) {
	                String userEmail = jwtUtils.getUserEmail(token);
	                String role = jwtUtils.getRole(token);

	                User user = new User();
	                user.setEmail(userEmail);
	                user.setRole(role);

	                CustomUserDetails customUserDetails = new CustomUserDetails(user);
	                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
	                        customUserDetails, null, customUserDetails.getAuthorities());
	                SecurityContextHolder.getContext().setAuthentication(authentication);
	            }
	            filterChain.doFilter(request, response);
	        } catch (ExpiredJwtException e) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.getWriter().write("{\"error\": \"Access token expired\"}");
	        } catch (Exception e) {
	            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	            response.getWriter().write("{\"error\": \"Invalid token\"}");
	        }
	    }
}
