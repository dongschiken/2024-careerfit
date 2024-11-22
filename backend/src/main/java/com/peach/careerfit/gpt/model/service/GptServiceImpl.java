package com.peach.careerfit.gpt.model.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.io.ResolverUtil.Test;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.peach.careerfit.component.RegexComponent;
import com.peach.careerfit.gpt.model.dao.GptDao;
import com.peach.careerfit.gpt.model.dto.GptRequest;
import com.peach.careerfit.gpt.model.dto.GptResponse;
import com.peach.careerfit.gpt.model.dto.GptResponse.Choice;
import com.peach.careerfit.meal.model.dao.MealDao;
import com.peach.careerfit.meal.model.dto.Meal;

@Service
public class GptServiceImpl implements GptService {

	private final static String MODEL = "gpt-4o-mini";
	private final static String API_URL = "https://api.openai.com/v1/chat/completions";
	private final GptDao gptDao;
	private final RegexComponent regexComponent;
	private final RestTemplate restTemplate;
	private final GptChatHistoryService gptChatHistoryService;
	private final MealDao mealDao;

	public GptServiceImpl(GptDao gptDao, RestTemplate restTemplate, GptChatHistoryService gptChatHistoryService,
			RegexComponent regexComponent, MealDao mealDao) {
		this.restTemplate = restTemplate;
		this.gptDao = gptDao;
		this.gptChatHistoryService = gptChatHistoryService;
		this.regexComponent = regexComponent;
		this.mealDao = mealDao;
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
		gptChatHistoryService.saveMessage(redisKey, "system :" + createSystemMessage(
				stringBuffer.toString() + "현재 날짜는" + LocalDateTime.now().plusDays(1).toString() + "야"));
		GptResponse response = restTemplate.postForObject(API_URL, gptRequest, GptResponse.class);
		return response;
	}

	@Override
	public GptResponse registAndRequestGpt(int userId, String message) {
		String redisKey = "gpt_chat_history" + userId;
		gptChatHistoryService.saveMessage(redisKey, message);
		GptResponse gptResponse = null;
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
			gptResponse = restTemplate.postForObject(API_URL,
					Map.of("model", MODEL, "messages", gptMessages), // **messages로 수정**
					GptResponse.class);
			for (Choice responses : gptResponse.getChoices()) {
				String content = responses.getMessage().getContent();
				gptChatHistoryService.saveMessage(redisKey, "assistant :" + content);
			}
			checkMealData(gptResponse, userId);
			return gptResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gptResponse;
	}

	private boolean checkMealData(GptResponse gptResponse, int userId) {
		try {
			for (Choice choice : gptResponse.getChoices()) {
				String message = choice.getMessage().getContent();
				if ((message.contains("아침") || message.contains("점심") || message.contains("저녁"))
						&& message.contains("날짜") && message.contains("식단") && message.contains("kcal")) {			
					List<Meal> meals = regexComponent.regexMeal(message, userId);
					Set<String> mealDates = new LinkedHashSet<>();
					for (Meal meal : meals) {
						mealDates.add(meal.getDate());
					}
					mealDao.deleteUserMeals(userId, mealDates);
					int status = mealDao.insertUserMeals(meals);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public String createSystemMessage(String messageContent) {
		return "{ \"message\":\"" + messageContent + "\" }";
	}
}
