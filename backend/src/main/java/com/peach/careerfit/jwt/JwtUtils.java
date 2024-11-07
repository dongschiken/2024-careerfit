package com.peach.careerfit.jwt;

import static io.jsonwebtoken.Jwts.builder;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
	
	private SecretKey secretKey;
	public static final String AUTHORIZATION_HEADER="Authorization";//헤더 이름
	public static final long ACCESS_TOKEN_VALIDATE = 60L * 60L;
	/**
     * @Value 어노테이션을 사용하여 application.properties 파일에서 JWT 비밀키를 주입받는다.
     * HS256 알고리즘을 사용하여 SecretKey 객체를 생성한다.
     */
    public JwtUtils(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SIG.HS256.key().build().getAlgorithm());
    }
    
    /**
     * 토큰 유효성 검증 메서드
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
    	// 만약 예외가 발생하면 false를 리턴, 예외 발생 x -> true를 리턴
    	try {
			Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
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
     * 주어진 토큰에서 "userId" 클레임을 추출한다.
     * 토큰을 파싱하고 검증한 후, 페이로드에서 userId를 가져온다.
     */
    public String getUserEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userId", String.class);
    }

    /**
     * 주어진 토큰에서 "role" 클레임을 추출한다.
     * 토큰을 파싱하고 검증한 후, 페이로드에서 role을 가져온다.
     */
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    /**
     * 토큰의 만료 여부를 확인한다.
     * 토큰의 만료 시간이 현재 시간보다 이전이면 true를 반환한다.
     */
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration()
                .before(new Date());
    }
    
    /**
     * access token을 추출하는 메서드
     * @param httpServletRequest
     * @return
     */
    public String getAccessToken(HttpServletRequest httpServletRequest) {
    	String bearerToken = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
    	if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Beadrer ")) {
    		return bearerToken.substring(7);
    	}
    	return null;
    }
    
    /**
     * 새로운 JWT를 생성한다.
     * memberId와 role을 클레임으로 추가한다.
     * 토큰 발행 시간(issuedAt)과 만료 시간(expiration)을 설정한다.
     * secretKey를 사용하여 토큰에 서명
     */
    public String createJwt(int userId, String role, String email, String nickname, Long expiredMs) {
        Date now = new Date();
//        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_VALIDATE);
    	return builder()
                .claim("userId", userId)
                .claim("role", role)
                .claim("email", email)
                .claim("nickname", nickname)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
