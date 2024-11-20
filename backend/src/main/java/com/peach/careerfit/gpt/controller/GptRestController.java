package com.peach.careerfit.gpt.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.peach.careerfit.Test;
import com.peach.careerfit.gpt.model.dto.Gpt;
import com.peach.careerfit.gpt.model.dto.GptRequest;
import com.peach.careerfit.gpt.model.dto.GptResponse;
import com.peach.careerfit.gpt.model.dto.Message;
import com.peach.careerfit.gpt.model.service.GptService;
import com.peach.careerfit.jwt.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/gpt")
public class GptRestController {

	private final RestTemplate restTemplate;
	private final static String MODEL = "gpt-3.5-turbo";
	private final static String API_URL = "https://api.openai.com/v1/chat/completions";
	private final JwtUtils jwtUtils;
	private final GptService gptService;

	public GptRestController(@Qualifier("openapiRestTemplate") RestTemplate restTemplate, JwtUtils jwtUtils,
			GptService gptService) {
		this.restTemplate = restTemplate;
		this.jwtUtils = jwtUtils;
		this.gptService = gptService;
	}

	// 처음 gpt 페이지를 오픈하면 안녕하세요 ~~ 하는 gpt를 받아와야함
	@PostMapping("/first")
	public ResponseEntity<Object> initialGpt() {
		StringBuffer stringBuffer = new StringBuffer();
		try (InputStream inputStream = Test.class.getClassLoader().getResourceAsStream("prompt"); 
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
		            if (inputStream == null) {
		                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("파일을 찾을 수 없습니다.");
		            }
		            String line;
		            while ((line = reader.readLine()) != null) {
		            	stringBuffer.append(line);
		            }
			} catch (Exception e) {
				e.printStackTrace();
			}
		GptRequest gptRequest = new GptRequest(MODEL, stringBuffer.toString(), "system");
		System.out.println(gptRequest);
		restTemplate.postForObject(API_URL, gptRequest, GptResponse.class);
		gptRequest = new GptRequest(MODEL, "식단관리 도와줘", "user");
		System.out.println(gptRequest);
		GptResponse response = restTemplate.postForObject(API_URL, gptRequest, GptResponse.class);
		System.out.println("respsonse : " + response);
		return ResponseEntity.ok(response);
	}
	
	
	/**
	 * 회원이면 userGpt
	 * 회원아니면 gptGpt
	 * @param request
	 * @param prompt
	 * @return
	 */
	@PostMapping
	public ResponseEntity<Object> registMessage(HttpServletRequest request, @RequestBody String prompt) {
		String token = jwtUtils.getAccessToken(request);
		int userId = jwtUtils.getUserIdFromToken(token);
		Gpt userGpt = new Gpt().builder().userId(userId).context(prompt).isUserChat("Y").build();
		int status = gptService.registMessage(userGpt);
		GptRequest gptRequest = new GptRequest(MODEL, prompt, "user");
		System.out.println(gptRequest);
		GptResponse response = restTemplate.postForObject(API_URL, gptRequest, GptResponse.class);
		if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("GPT 메시지 응답 오류");
		}
		Gpt gptGpt = new Gpt().builder().userId(userId).context(response.getChoices().get(0).getMessage().getContent()).isUserChat("N").build();
		gptService.registMessage(gptGpt);
		System.out.println(userGpt);
		System.out.println(gptGpt);
		return ResponseEntity.status(HttpStatus.OK).body(gptGpt);
	}

}
