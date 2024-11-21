package com.peach.careerfit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
	public static void main(String[] args) {
		int count = 0;
		if ((str.contains("아침") || str.contains("점심") || str.contains("저녁")) && str.contains("날짜") && str.contains("식단")
				&& str.contains("kcal")) {
			List<Map<String, Object>> mealsPlan = new ArrayList<>();
			Pattern dayPattern = Pattern.compile("\\s+날짜:\\s+(\\d{4}-\\d{2}-\\d{2})");
			Matcher dayMatcher = dayPattern.matcher(str);
			while (dayMatcher.find()) {
				count++;
				Map<String, Object> dayPlan = new HashMap<>();
				String date = dayMatcher.group(1).trim();
				String[] meals = { "아침:", "점심:", "저녁:" };
				for (int i = 0; i < meals.length; i++) {
					String mealType = meals[i];
					Map<String, List<Map<String, Object>>> mealsData = new HashMap<>();
					String mealRegex = mealType + "\\s+(.*?)(\\s+" + (i + 1 < meals.length ? meals[i + 1] : "이 식단도|$") + ")";
		            Pattern mealPattern = Pattern.compile(mealRegex, Pattern.DOTALL);
		            Matcher mealMatcher = mealPattern.matcher(str.substring(dayMatcher.start()));
		            if (mealMatcher.find()) {
		                String mealContent = mealMatcher.group(1).trim();
		                System.out.println("매칭 성공 (" + mealType + "): " + mealContent);
		                List<Map<String, Object>> foods = extractFoods(mealContent);
		                mealsData.put(mealType.replace(":", ""), foods);
		            } else {
		                System.out.println("매칭 실패 (" + mealType + ")");
		            }
		            dayPlan.put("date", date);
		            dayPlan.put("meals", mealsData);
		            mealsPlan.add(dayPlan);
		            System.out.println(mealsPlan);
				}
			}
			System.out.println(count);
		}
	}

	private static List<Map<String, Object>> extractFoods(String mealContent) {
		 List<Map<String, Object>> foods = new ArrayList<>();
	        String[] lines = mealContent.split("\\r?\\n");
	        for (String line : lines) {
	            if (line.trim().isEmpty()) continue;
//	            System.out.println("음식 : " + line);
	            // 음식 데이터 정규식
	            Pattern foodPattern = Pattern.compile("(.*?)(\\((\\d+)\\s*kcal,\\s*(\\d+)\\s*g,\\s*(\\d+)\\s*g,\\s*(\\d+)\\s*g\\))");
	            Matcher foodMatcher = foodPattern.matcher(line);
//	            System.out.println(foodMatcher);
	            if (foodMatcher.find()) {
//	            	System.out.println("음식을 찾음");
	                Map<String, Object> food = new HashMap<>();
	                food.put("foodName", foodMatcher.group(1).trim());
	                food.put("calories", Integer.parseInt(foodMatcher.group(3)));
	                food.put("protein", Integer.parseInt(foodMatcher.group(4)));
	                food.put("fat", Integer.parseInt(foodMatcher.group(5)));
	                food.put("carbs", Integer.parseInt(foodMatcher.group(6)));
	                foods.add(food);
	            }
	        }
	        return foods;
	}

	public static String str = "감사합니다! 회원님의 식습관을 잘 알게 되었어요. 과식을 자주하고 배달음식을 많이 드신다면, 체중 감량 목표를 위해서는 조금씩 개선이 필요할 것 같아요. 그럼 일주일 동안의 식단을 준비해볼게요!\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "날짜: 2024-11-21\r\n"
			+ "아침:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "오트밀 (150 kcal, 5 g, 3 g, 27 g)  \r\n"
			+ "\r\n"
			+ "바나나 (90 kcal, 1 g, 0 g, 23 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "점심:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "현미밥 (210 kcal, 5 g, 1 g, 44 g)  \r\n"
			+ "\r\n"
			+ "닭가슴살구이 (150 kcal, 31 g, 3 g, 0 g)  \r\n"
			+ "\r\n"
			+ "브로콜리 (55 kcal, 4 g, 0 g, 11 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "저녁:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "두부조림 (180 kcal, 15 g, 10 g, 8 g)  \r\n"
			+ "\r\n"
			+ "미역국 (40 kcal, 4 g, 2 g, 5 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "날짜: 2024-11-22\r\n"
			+ "아침:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "요거트 (100 kcal, 5 g, 3 g, 15 g)  \r\n"
			+ "\r\n"
			+ "사과 (80 kcal, 0 g, 0 g, 22 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "점심:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "퀴노아샐러드 (200 kcal, 8 g, 5 g, 36 g)  \r\n"
			+ "\r\n"
			+ "삶은 달걀 (70 kcal, 6 g, 5 g, 0 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "저녁:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "연어구이 (250 kcal, 25 g, 15 g, 0 g)  \r\n"
			+ "\r\n"
			+ "고구마 (120 kcal, 2 g, 0 g, 28 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "날짜: 2024-11-23\r\n"
			+ "아침:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "스크램블 에그 (180 kcal, 12 g, 14 g, 2 g)  \r\n"
			+ "\r\n"
			+ "통밀빵 (80 kcal, 4 g, 1 g, 15 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "점심:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "비빔밥 (300 kcal, 15 g, 10 g, 45 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "저녁:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "소고기무국 (220 kcal, 20 g, 8 g, 5 g)  \r\n"
			+ "\r\n"
			+ "쌈채소 (30 kcal, 2 g, 0 g, 6 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "날짜: 2024-11-24\r\n"
			+ "아침:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "치아씨드푸딩 (150 kcal, 5 g, 5 g, 25 g)  \r\n"
			+ "\r\n"
			+ "키위 (60 kcal, 1 g, 0 g, 14 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "점심:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "닭가슴살샐러드 (250 kcal, 30 g, 8 g, 10 g)  \r\n"
			+ "\r\n"
			+ "방울토마토 (30 kcal, 2 g, 0 g, 6 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "저녁:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "된장찌개 (180 kcal, 8 g, 6 g, 25 g)  \r\n"
			+ "\r\n"
			+ "밥 (250 kcal, 5 g, 0 g, 55 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "날짜: 2024-11-25\r\n"
			+ "아침:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "과일 스무디 (200 kcal, 4 g, 2 g, 40 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "점심:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "감자전 (250 kcal, 5 g, 10 g, 35 g)  \r\n"
			+ "\r\n"
			+ "오이 무침 (50 kcal, 2 g, 0 g, 10 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "저녁:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "가지 볶음 (200 kcal, 5 g, 10 g, 20 g)  \r\n"
			+ "\r\n"
			+ "밥 (250 kcal, 5 g, 0 g, 55 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "날짜: 2024-11-26\r\n"
			+ "아침:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "바나나 오트밀 팬케이크 (200 kcal, 5 g, 3 g, 35 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "점심:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "불고기 (300 kcal, 25 g, 15 g, 10 g)  \r\n"
			+ "\r\n"
			+ "쌈채소 (30 kcal, 2 g, 0 g, 6 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "저녁:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "해물파전 (300 kcal, 10 g, 10 g, 40 g)  \r\n"
			+ "\r\n"
			+ "미역국 (40 kcal, 4 g, 2 g, 5 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "날짜: 2024-11-27\r\n"
			+ "아침:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "시리얼 (150 kcal, 3 g, 2 g, 30 g)  \r\n"
			+ "\r\n"
			+ "우유 (100 kcal, 8 g, 5 g, 12 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "점심:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "김치찌개 (200 kcal, 10 g, 10 g, 15 g)  \r\n"
			+ "\r\n"
			+ "밥 (250 kcal, 5 g, 0 g, 55 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "저녁:  \r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "버섯볶음 (150 kcal, 5 g, 7 g, 15 g)  \r\n"
			+ "\r\n"
			+ "삶은 브로콜리 (55 kcal, 4 g, 0 g, 11 g)\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "이렇게 일주일 동안의 식단을 준비했습니다! 이 식단으로 체중 감량에 도움이 되길 바래요. 다른 질문이 있으면 언제든지 말씀해 주세요! 쀼!";

}
