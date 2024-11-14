//package com.peach.careerfit.interceptor;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import com.peach.careerfit.jwt.JwtUtils;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class JwtInterceptor implements HandlerInterceptor {
//	
//	private final JwtUtils jwtUtils;
//	
//	public JwtInterceptor(JwtUtils jwtUtils) {
//		this.jwtUtils = jwtUtils;
//	}
//
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//		String uri = request.getRequestURI();
//		String accessToken = jwtUtils.getAccessToken(request); 
//		String requestUri = request.getRequestURI();
//		if(accessToken == null) {
//			System.out.println("비회원");
//			return true;
//		}else {
//			System.out.println("access 존재");
//			if(jwtUtils.validateToken(accessToken)) {
//				System.out.println("유효 토큰");
//				return true;
//			}else {
//				System.out.println("유효하지 않은 토큰");
//				return false;
//			}
//		}
//	}
//	
//	
//}
