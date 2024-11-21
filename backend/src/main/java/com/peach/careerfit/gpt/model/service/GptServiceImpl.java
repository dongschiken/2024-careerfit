package com.peach.careerfit.gpt.model.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	private final static String MODEL = "gpt-4o";
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
		gptChatHistoryService.saveMessage(MODEL, "system:"+stringBuffer.toString());
		GptResponse response = restTemplate.postForObject(API_URL, gptRequest, GptResponse.class);
		return response;
	}

	@Override
	public GptResponse registAndRequestGpt(int userId, String message) {
		String redisKey = "gpt_chat_history" + userId;
		gptChatHistoryService.saveMessage(redisKey, message);
		List<Object> userHistory = gptChatHistoryService.getAllMessages(redisKey);
		List<Map<String, String>> gptMessages = new ArrayList<>();
		System.out.println(userHistory);
		// GPT 요청 생성
		if(userHistory != null && !userHistory.isEmpty()) {
			for (Object history : userHistory) {
				System.out.println(history);
				if(history.toString().startsWith("user") ) {
					gptMessages.add(Map.of("role", "user", "content", history.toString().substring(5)));
				}else if(history.toString().startsWith("assistance")){
					gptMessages.add(Map.of("role", "assistance", "content", history.toString().substring(11)));
				}else if(history.toString().startsWith("system")) {
					gptMessages.add(Map.of("role", "system", "content", history.toString().substring(7)));
				}
			}
		}
		System.out.println(gptMessages);
		gptMessages.add(Map.of("role", "user", "content", message));
		GptResponse gptResponse = restTemplate.postForObject(API_URL, Map.of("model", MODEL, "message", gptMessages), GptResponse.class);
		for (Choice responses : gptResponse.getChoices()) {
			gptChatHistoryService.saveMessage(redisKey, responses.getMessage().getRole() + ":" + responses.getMessage().getContent());
		}
		return gptResponse;
	}

}
