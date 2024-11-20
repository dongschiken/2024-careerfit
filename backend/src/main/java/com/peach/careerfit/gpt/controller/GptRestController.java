//package com.peach.careerfit.gpt.controller;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import com.peach.careerfit.gpt.model.dto.GptRequest;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/api/gpt")
//public class GptRestController {
//	
//    @Value("${openai.api.key}")
//    private String apiKey;
//	private final RestTemplate restTemplate;
//	private final String URL = "https://api.openai.com/v1/chat/completions";
//	
//	public GptRestController(RestTemplate restTemplate) {
//		this.restTemplate = restTemplate;
//	}
//	//
//	
//	
//	@PostMapping
//	public ResponseEntity<Object> registMessage(@RequestBody GptRequest gptRequest) {
//		System.out.println(gptRequest.toString());
//		Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", "gpt-3.5-turbo");
//        requestBody.put("messages", new Object[]{
//            Map.of("role", "user", "content", gptRequest.getMessage())
//        });
//        
//        // 헤더 설정
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization", "Bearer " + apiKey);
//        headers.put("Content-Type", "application/json");
//        ResponseEntity<Map> response = restTemplate.postForEntity(URL, requestBody, Map.class, headers);
//        String reply = (String) ((Map) ((List) response.getBody().get("choices")).get(0)).get("text");
//        Map<String, String> result = new HashMap<>();
//        result.put("reply", reply);
//		return ResponseEntity.status(HttpStatus.OK).body(result);
//	}
//	
//}
