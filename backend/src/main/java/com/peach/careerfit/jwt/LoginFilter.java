package com.peach.careerfit.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.peach.careerfit.user.model.dao.UserMapper;
import com.peach.careerfit.user.model.dto.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    @Autowired
    public LoginFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
    }

    /**
     * Authentication manager 한테 유저 id, password -> 토큰에 담아서 던져주면 로그인 실패, 성공 여부에 따라서 처리
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 클라이언트 요청에서 userEmail, password 추출
        String userEmail = request.getParameter("userEmail");
        String password = obtainPassword(request);
        // 3번째 매개변수는 ROLE 값
        
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userEmail, password);

        return authenticationManager.authenticate(authToken);
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

        String token = jwtUtils.createJwt(user.getUserId(), user.getRole(), user.getEmail(), user.getNickname(), 60*60*10L);
        response.addHeader("Authorization", "Bearer " + token); // Bearer_ 뒤에 띄어쓰기 한칸
    }
    
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(401); // 실패하면 401 응답
    }
}
