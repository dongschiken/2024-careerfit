package com.peach.careerfit.config;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.peach.careerfit.jwt.JwtFilter;
import com.peach.careerfit.jwt.JwtUtils;
import com.peach.careerfit.jwt.LoginFilter;
import com.peach.careerfit.user.model.dao.UserMapper;

import jakarta.servlet.http.HttpServletRequest;


@EnableWebSecurity // 웹 시큐리티를 위한 클래스
@Configuration // 스프링한테 config 클래스라는것을 알려줌
public class SecurityConfig  {

    private final JwtUtils jwtUtils;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserMapper userMapper;
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration
    		, JwtUtils jwtUtils, UserMapper userMapper
    		) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 비밀번호를 캐시로 암호화 해서 검증을 진행해야해서
     * 아래의 메서드로 암호화 진행한다.
     * @return BCryptPasswordEncoder
     */
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf((auth) -> auth.disable()); // JWT 방식은 csrf 공격을 방어하지 않아도 된다.
        
        // Form 로그인 방식 disable
        http.formLogin((auth) -> auth.disable());

        // http basic 인증 방식 disable
        http.httpBasic((auth) -> auth.disable());
        // JWT 인증 / 인가를 위해 필요한 코드
        // 경로별 인가 작업
        // static한 값들에 대해서도 경로를 지정해 줘야한다.
        http.authorizeHttpRequests((auth) -> auth
        		.requestMatchers("/api/login/**", "/api/join", "/api/login", "/error" , "/main", "/api/user/**", "/api/chat-room/**", "/api/chat-rooms/**").permitAll()
        		.requestMatchers(HttpMethod.GET, "/api/board").permitAll()
                .requestMatchers("/assets/**", "/js/**", "/img/**").permitAll() // 정적 리소스 접근 허용
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/board", "/api/board/category").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/board/**").hasRole("USER")                
                .requestMatchers(HttpMethod.DELETE, "/api/board/**").hasRole("USER")                                
                .anyRequest().authenticated());
//
        
        http.cors((cors) -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowCredentials(true);
                corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(List.of("*"));
                corsConfiguration.setExposedHeaders(Collections.singletonList("Authorization"));
                return corsConfiguration;
            }
        }));

        // 1번 매개변수 : 사용자가 만든 필터 ( 등록할 필터 )
        // 2번 매개변수 : 실제로 등록될 필터 지점
        // 로그인 필터는 매니저가 필요하다 -> 매니저는 configuration 필요하다.
        // jwt를 위한 jwtUtil 도 필요하다.
        http.addFilterAt(
                new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtils, userMapper),
                UsernamePasswordAuthenticationFilter.class
        );
        http.addFilterAt(
                new JwtFilter(jwtUtils), LoginFilter.class
        );
        // 세션을 스테이트 리스 상태로 관리하기 위한 코드
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/assets/**", "/js/**", "/img/**");
    }

}
