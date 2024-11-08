package com.peach.careerfit.jwt;

import static io.jsonwebtoken.Jwts.builder;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

    private SecretKey secretKey;
    public static final String AUTHORIZATION_HEADER = "Authorization"; // 헤더 이름
    public static final long ACCESS_TOKEN_VALIDATE = 1000L * 60 * 60;
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 15; // 15일로 설정
    /**
     * @Value 어노테이션을 사용하여 application.properties 파일에서 JWT 비밀키를 주입받는다.
     * HS256 알고리즘을 사용하여 SecretKey 객체를 생성한다.
     */
    public JwtUtils(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    /**
     * 토큰 유효성 검증 메서드
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            System.out.println("잘못된 토큰 서명입니다.");
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 토큰입니다.");
        } catch (IllegalArgumentException | MalformedJwtException e) {
            System.out.println("잘못된 토큰입니다.");
        }
        return false;
    }

    /**
     * 주어진 토큰에서 "email" 클레임을 추출한다.
     */
    public String getUserEmail(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.get("email", String.class);
    }

    /**
     * 주어진 토큰에서 "role" 클레임을 추출한다.
     */
    public String getRole(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.get("role", String.class);
    }

    /**
     * 토큰의 만료 여부를 확인한다.
     */
    public Boolean isExpired(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.getExpiration().before(new Date());
    }

    /**
     * access token을 추출하는 메서드
     */
    public String getAccessToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    /**
     * 새로운 Access 토큰을 생성
     */
    public String createJwt(String role, String email, String nickname) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_VALIDATE);
        return builder()
                .claim("role", role)
                .claim("email", email)
                .claim("nickname", nickname)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();
    }
    
    /**
     * 새로운 refresh 토큰을 생성
     */
    public String craeteRefreshToken(int userId, String role, String email, String nickname, Long expiredMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);   
    	return builder()
                   .claim("userId", userId)
                   .claim("role", role)
                   .claim("email", email)
                   .claim("nickname", nickname)
                   .setIssuedAt(now)
                   .setExpiration(expiration)
                   .signWith(secretKey)
                   .compact();
    }
}

