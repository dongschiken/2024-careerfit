package com.peach.careerfit.gpt.model.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.peach.careerfit.Test;
import com.peach.careerfit.gpt.model.dao.GptDao;
import com.peach.careerfit.gpt.model.dto.GptRequest;
import com.peach.careerfit.gpt.model.dto.GptResponse;
import com.peach.careerfit.gpt.model.dto.GptResponse.Choice;

@Service
public class GptServiceImpl implements GptService {

	private final static String MODEL = "gpt-4o-mini";
	private final static String API_URL = "https://api.openai.com/v1/chat/completions";
	private final GptDao gptDao;
	private final RestTemplate restTemplate;
	private final GptChatHistoryService gptChatHistoryService;

	public GptServiceImpl(GptDao gptDao, RestTemplate restTemplate, GptChatHistoryService gptChatHistoryService) {
		this.restTemplate = restTemplate;
		this.gptDao = gptDao;
		this.gptChatHistoryService = gptChatHistoryService;
	}

	@Override
	public GptResponse initailGpt(int userId) {
		StringBuffer stringBuffer = new StringBuffer();
		String redisKey = "gpt_chat_history" + userId;
		gptChatHistoryService.removeAllMessages(redisKey);
		try (InputStream inputStream = Test.class.getClassLoader().getResourceAsStream("prompt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			if (inputStream == null) {
				return null;
			}
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuffer.append(line).append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		GptRequest gptRequest = new GptRequest(MODEL, stringBuffer.toString(), "system");
		gptChatHistoryService.saveMessage(redisKey, "system :"+createSystemMessage(stringBuffer.toString()+"현재 날짜는"+LocalDateTime.now().toString()+"야"));
		GptResponse response = restTemplate.postForObject(API_URL, gptRequest, GptResponse.class);
		return response;
	}

	@Override
	public GptResponse registAndRequestGpt(int userId, String message) {
		String redisKey = "gpt_chat_history" + userId;
		gptChatHistoryService.saveMessage(redisKey,  message);
		List<Object> userHistory = gptChatHistoryService.getAllMessages(redisKey);
		List<Map<String, String>> gptMessages = new ArrayList<>();
		
		if (userHistory != null && !userHistory.isEmpty()) {
			for (Object history : userHistory) {
				String historyString = history.toString();
				if (historyString.startsWith("user ")) {
					gptMessages.add(Map.of("role", "user", "content", historyString.substring(6)));
				} else if (historyString.startsWith("assistant ")) {
					gptMessages.add(Map.of("role", "assistant", "content", historyString.substring(11)));
				} else if (historyString.startsWith("system ")) {
					gptMessages.add(Map.of("role", "system", "content", historyString.substring(11)));					
				}
			}
		}
		gptMessages.add(Map.of("role", "user", "content", message));
		try {
			// OpenAI GPT API 요청 생성
			GptResponse gptResponse = restTemplate.postForObject(API_URL,
					Map.of("model", MODEL, "messages", gptMessages), // **messages로 수정**
					GptResponse.class);
			for (Choice responses : gptResponse.getChoices()) {
				String content = responses.getMessage().getContent(); 
				gptChatHistoryService.saveMessage(redisKey,
						"assistant :"+content);
			}
			return gptResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("응답 못받고 나옴");
		return null;
	}
	
	public String createSystemMessage(String messageContent) {
	    return "{ \"message\":\"" + messageContent + "\" }";
	}
}
