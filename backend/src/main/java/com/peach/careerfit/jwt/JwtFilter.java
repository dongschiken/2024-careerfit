package com.peach.careerfit.jwt;


import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.peach.careerfit.user.model.dto.CustomUserDetails;
import com.peach.careerfit.user.model.dto.User;

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
        // request header에서 Authorization를 찾는다. ( JWT 토큰 )
        String authorization = request.getHeader("Authorization");

        // Authorization을 검증
        if(authorization == null || !authorization.startsWith("Bearer")) {
            System.out.println("token null");
            filterChain.doFilter(request, response);

            // 조건에 해당되면 메서드 종료 ( 필수 )
            return;
        }
        // Bearer 제거후 순수 토큰만 가져옴
        String token = authorization.split(" ")[1];

        // 토큰 소멸시간 검증
        if(jwtUtils.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userEmail = jwtUtils.getUserEmail(token);
        String role = jwtUtils.getRole(token);

        // 회원엔티티 생성해서 값 set
        User user = new User();
        user.setEmail(userEmail);
        user.setPassword("temppassword");
        user.setRole(role);
        
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
