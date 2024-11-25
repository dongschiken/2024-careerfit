package com.peach.careerfit.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/kakao")
public class KakaoController {

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.client.secret}")
    private String kakaoClientSecret;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    @PostMapping("/callback")
    public ResponseEntity<?> kakaoLoginCallback(@RequestBody Map<String, String> requestBody) {
        String code = requestBody.get("code");

        String kakaoTokenUrl = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();

        System.out.println(code);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId); // REST API 키
        params.add("redirect_uri", kakaoRedirectUri); // Kakao Developers에 등록된 Redirect URI
        params.add("code", code); // 프론트엔드에서 전달된 인증 코드

        
        System.out.println("Request Parameters:");
        System.out.println("grant_type: authorization_code");
        System.out.println("client_id: " + kakaoClientId);
        System.out.println("redirect_uri: " + kakaoRedirectUri);
        System.out.println("code: " + code);

        
        // client_secret 제거
        // params.put("client_secret", kakaoClientSecret); // 삭제

        ResponseEntity<Map> response;
        try {
        	HttpHeaders headers = new HttpHeaders();
        	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        	HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            response = restTemplate.postForEntity(kakaoTokenUrl, request, Map.class);
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kakao API 호출 실패: " + e.getMessage());
        }

        Map<String, Object> kakaoToken = response.getBody();
        if (kakaoToken == null || !kakaoToken.containsKey("access_token")) {
            return ResponseEntity.badRequest().body("Kakao 토큰 응답 실패: " + kakaoToken);
        }

        String accessToken = kakaoToken.get("access_token").toString();
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }



    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String accessToken) {
        String kakaoUserInfoUrl = "https://kapi.kakao.com/v2/user/me";
        RestTemplate restTemplate = new RestTemplate();

        // Authorization 헤더 설정
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);

        ResponseEntity<Map> response = restTemplate.postForEntity(kakaoUserInfoUrl, headers, Map.class);

        if (response.getBody() == null) {
            return ResponseEntity.badRequest().body("사용자 정보 가져오기 실패");
        }

        Map<String, Object> kakaoUserInfo = response.getBody();
        return ResponseEntity.ok(kakaoUserInfo);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> kakaoLogout(@RequestHeader("Authorization") String accessToken) {
        String kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/logout";

        // Kakao API 호출을 위한 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken); // Bearer 토큰

        // 요청 생성
        HttpEntity<Void> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoLogoutUrl, request, String.class);
            System.out.println("Kakao Logout Response: " + response.getBody());

            // 클라이언트 세션 및 저장된 인증 정보 삭제 (Optional)
            // 여기서 Redis나 Database의 Refresh Token도 제거해야 한다면 추가 작업 필요

            return ResponseEntity.ok("Kakao 로그아웃 성공");
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("Kakao 로그아웃 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kakao 로그아웃 실패: " + e.getMessage());
        }
    }
    
    @PostMapping("/auth/kakao/unlink")
    public ResponseEntity<?> kakaoUnlink(@RequestHeader("Authorization") String accessToken) {
        String kakaoUnlinkUrl = "https://kapi.kakao.com/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Kakao API 호출
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoUnlinkUrl, request, String.class);
            System.out.println("Kakao Unlink Response: " + response.getBody());

            return ResponseEntity.ok("Kakao 연결 해제 성공");
        } catch (Exception e) {
        	
            System.out.println("Kakao 연결 해제 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kakao 연결 해제 실패: " + e.getMessage());
        }
    }

    

}
