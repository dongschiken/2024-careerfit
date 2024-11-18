package com.peach.careerfit.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peach.careerfit.auth.model.service.RefreshTokenService;
import com.peach.careerfit.user.model.dao.UserMapper;
import com.peach.careerfit.user.model.dto.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
	
	public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 15; // 15일로 설정
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;
    
    public LoginFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserMapper userMapper, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
        this.refreshTokenService = refreshTokenService;
    }
    
    @Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
    	
    	 ObjectMapper objectMapper = new ObjectMapper();
         // request의 InputStream에서 JSON 데이터를 Map으로 변환
         Map<String, String> credentials;
         UsernamePasswordAuthenticationToken authRequest = null;
		try {
			credentials = objectMapper.readValue(request.getInputStream(), Map.class);
	         String email = credentials.get("email");
	         String password = credentials.get("password");
	         authRequest = new UsernamePasswordAuthenticationToken(email, password);
		} catch (IOException e) {
			e.printStackTrace();
		}
         return authenticationManager.authenticate(authRequest);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
	    
	    String userEmail = authResult.getName();
	    User user = userMapper.findByUserEmail(userEmail);

	    // role을 뽑아내기 위해 collection 형태로 변환 후 role을 뽑아냄
	    Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)authResult.getAuthorities();
	    Iterator<GrantedAuthority> iterator = authorities.iterator();
	    GrantedAuthority auth = iterator.next();
	    String role = auth.getAuthority();
	    
	    // Access Token 생성
	    String accessToken = jwtUtils.createJwt(user.getUserId(), role, user.getEmail(), user.getNickname());
	    
	    // Refresh Token 생성 및 저장 (Redis)
	    String refreshToken = jwtUtils.craeteRefreshToken(user.getUserId(), role, userEmail, user.getNickname());
	    refreshTokenService.saveRefreshToken(userEmail, refreshToken, REFRESH_TOKEN_EXPIRE_TIME);
	    // Refresh Token을 HttpOnly 쿠키에 추가
	    Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
	    refreshTokenCookie.setHttpOnly(true); // HttpOnly 설정으로 클라이언트 자바스크립트에서 접근 차단
	    refreshTokenCookie.setMaxAge((int) (JwtUtils.REFRESH_TOKEN_EXPIRE_TIME / 1000)); // 쿠키 만료 시간 설정
	    refreshTokenCookie.setPath("/"); // 모든 경로에서 사용 가능하도록 설정
	    refreshTokenCookie.setSecure(false);
	    // Access Token과 Refresh Token을 JSON 형태로 응답에 추가
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.addCookie(refreshTokenCookie);
	    Map<String, Object> tokens = new HashMap<>();
	    tokens.put("accessToken", accessToken);
	    new ObjectMapper().writeValue(response.getWriter(), tokens);
	}
    
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(401); // 실패하면 401 응답
    }
}
