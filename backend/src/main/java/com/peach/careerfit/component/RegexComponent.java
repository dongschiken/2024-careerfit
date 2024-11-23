package com.peach.careerfit.component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.peach.careerfit.meal.model.dto.Meal;

@Component
public class RegexComponent {

	public static List<Meal> regexMeal(String mealText, int userId) {
		List<Meal> meals = new ArrayList<>();
		// 날짜 패턴
		Pattern datePattern = Pattern.compile("### 날짜: (\\d{4}-\\d{2}-\\d{2})");

		// 식사 섹션 패턴 (아침, 점심, 저녁)
		Pattern mealTypePattern = Pattern.compile("(?:(\\*\\*아침:)|아침:)\\s*([\\s\\S]*?)(?=\\*\\*점심:|점심:|\\Z)");

		// 식사 항목 패턴
		Pattern mealItemPattern = Pattern.compile(
				"-\\s*(.+?)\\s*\\((\\d+)\\s*kcal,\\s*(\\d+(?:\\.\\d+)?)g,\\s*(\\d+(?:\\.\\d+)?)g,\\s*(\\d+(?:\\.\\d+)?)g\\)");

		Matcher dateMatcher = datePattern.matcher(mealText);

		while (dateMatcher.find()) {
			String date = dateMatcher.group(1); // 날짜 추출

			// 해당 날짜 이후의 텍스트 추출
			int startIndex = dateMatcher.end();
			int endIndex = mealText.indexOf("### 날짜:", startIndex);
			String dayText = (endIndex == -1) ? mealText.substring(startIndex)
					: mealText.substring(startIndex, endIndex);

			// 식사 섹션 추출 (아침, 점심, 저녁)
			String[] mealTypes = { "아침", "점심", "저녁" };
			for (String type : mealTypes) {
				Pattern typePattern = Pattern.compile("(?:(\\*\\*" + type + ":)|" + type
						+ ":)\\s*([\\s\\S]*?)(?=(\\*\\*아침:|\\*\\*점심:|\\*\\*저녁:|아침:|점심:|저녁:|\\Z))");
				Matcher mealTypeMatcher = typePattern.matcher(dayText);
				if (mealTypeMatcher.find()) {
					String mealSection = mealTypeMatcher.group(2);

					// 각 식사 항목 추출
					Matcher mealItemMatcher = mealItemPattern.matcher(mealSection);
					while (mealItemMatcher.find()) {
						String name = mealItemMatcher.group(1);
						int kcal = Integer.parseInt(mealItemMatcher.group(2));
						double protein = Double.parseDouble(mealItemMatcher.group(3));
						double fat = Double.parseDouble(mealItemMatcher.group(4));
						double carbs = Double.parseDouble(mealItemMatcher.group(5));
						meals.add(new Meal()
								.builder()
								.date(date)
								.userId(userId)
								.type(type)
								.name(name)
								.kcal(kcal)
								.protein(protein)
								.fat(fat)
								.carbs(carbs)
								.build());
					}
				}
			}
		}
		return meals;
	}
}
