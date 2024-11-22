package com.peach.careerfit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Meal {
    String date;
    String mealType;
    String foodName;
    int calories;
    double protein;
    double fat;
    double carbs;

    public Meal(String date, String mealType, String foodName, int calories, double protein, double fat, double carbs) {
        this.date = date;
        this.mealType = mealType;
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }

    @Override
    public String toString() {
        return String.format("Date: %s, Type: %s, Food: %s, Calories: %d, Protein: %.1f, Fat: %.1f, Carbs: %.1f", 
            date, mealType, foodName, calories, protein, fat, carbs);
    }
}

public class MealParser {
    public static List<Meal> parseMealData(String mealText) {
        List<Meal> meals = new ArrayList<>();

        // 날짜 패턴
        Pattern datePattern = Pattern.compile("### 날짜: (\\d{4}-\\d{2}-\\d{2})");

        // 식사 섹션 패턴 (아침, 점심, 저녁)
        Pattern mealTypePattern = Pattern.compile("(?:(\\*\\*아침:)|아침:)\\s*([\\s\\S]*?)(?=\\*\\*점심:|점심:|\\Z)");

        // 식사 항목 패턴
        Pattern mealItemPattern = Pattern.compile("-\\s*(.+?)\\s*\\((\\d+)\\s*kcal,\\s*(\\d+(?:\\.\\d+)?)g,\\s*(\\d+(?:\\.\\d+)?)g,\\s*(\\d+(?:\\.\\d+)?)g\\)");

        Matcher dateMatcher = datePattern.matcher(mealText);

        while (dateMatcher.find()) {
            String date = dateMatcher.group(1); // 날짜 추출

            // 해당 날짜 이후의 텍스트 추출
            int startIndex = dateMatcher.end();
            int endIndex = mealText.indexOf("### 날짜:", startIndex);
            String dayText = (endIndex == -1) ? mealText.substring(startIndex) : mealText.substring(startIndex, endIndex);

            // 식사 섹션 추출 (아침, 점심, 저녁)
            String[] mealTypes = {"아침", "점심", "저녁"};
            for (String mealType : mealTypes) {
                Pattern typePattern = Pattern.compile("(?:(\\*\\*" + mealType + ":)|" + mealType + ":)\\s*([\\s\\S]*?)(?=(\\*\\*아침:|\\*\\*점심:|\\*\\*저녁:|아침:|점심:|저녁:|\\Z))");
                Matcher mealTypeMatcher = typePattern.matcher(dayText);
                if (mealTypeMatcher.find()) {
                    String mealSection = mealTypeMatcher.group(2);

                    // 각 식사 항목 추출
                    Matcher mealItemMatcher = mealItemPattern.matcher(mealSection);
                    while (mealItemMatcher.find()) {
                        String foodName = mealItemMatcher.group(1);
                        int calories = Integer.parseInt(mealItemMatcher.group(2));
                        double protein = Double.parseDouble(mealItemMatcher.group(3));
                        double fat = Double.parseDouble(mealItemMatcher.group(4));
                        double carbs = Double.parseDouble(mealItemMatcher.group(5));

                        meals.add(new Meal(date, mealType, foodName, calories, protein, fat, carbs));
                    }
                }
            }
        }

        return meals;
    }

    public static void main(String[] args) {
        String mealText = """
        ### 날짜: 2024-11-22
        아침:  
        - 현미밥 (210 kcal, 4g, 1g, 45g)  
        - 계란 삶은 것 2개 (140 kcal, 12g, 10g, 0g)  

        점심:  
        - 닭 가슴살 구이 (180 kcal, 35g, 4g, 0g)  
        - 샐러드 (양상추, 토마토, 오이) (50 kcal, 2g, 0g, 10g)  

        저녁:  
        - 두부 찌개 (150 kcal, 15g, 9g, 6g)  
        - 쌈 채소 (40 kcal, 2g, 0g, 8g)  

        ### 날짜: 2024-11-23
        **아침:**
        - 귀리죽 (180 kcal, 6g, 3g, 30g)
        - 바나나 (90 kcal, 1g, 0g, 23g)

        **점심:**
        - 그릴드 닭가슴살 (250 kcal, 30g, 5g, 0g)
        - 샐러드 (60 kcal, 2g, 1g, 12g)

        **저녁:**
        - 김치찌개 (150 kcal, 8g, 7g, 12g)
        - 밥 (200 kcal, 4g, 0g, 44g)
        """;

        List<Meal> parsedMeals = parseMealData(mealText);

        for (Meal meal : parsedMeals) {
            System.out.println(meal);
        }
    }
}
