package com.peach.careerfit;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NaverNewsCrawler {

	public static void main(String[] args) {
		final String crawlingEnterUrl = "https://news.naver.com/breakingnews/section/103/241";
		Connection conn = Jsoup.connect(crawlingEnterUrl).userAgent(
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
				.referrer("https://news.naver.com/breakingnews/section/103/241").timeout(5000);

		try {
			Document document = conn.get();

			// 기사 제목과 링크 추출
			Elements titleElements = document.select(".sa_text_lede"); // 기사 제목
			Elements thumbElements = document.select("div.sa_thumb_inner a");
			for (int i = 0; i < Math.min(titleElements.size(), thumbElements.size()); i++) {
				// 제목
				String title = titleElements.get(i).text();
				Element element = thumbElements.get(i);
				// 기사 링크
				String articleLink = element.attr("href");
				// 이미지 URL (data-src에서 추출)
				Element imgElement = element.selectFirst("img");
				String imageUrl = imgElement != null ? imgElement.attr("data-src") : "이미지 없음";
				// "건강" 키워드 포함된 기사만 출력
				if (title.trim().toLowerCase().contains("건강") || title.trim().toLowerCase().contains("호흡")
						|| title.trim().toLowerCase().contains("식습관") || title.trim().toLowerCase().contains("운동")
						|| title.trim().toLowerCase().contains("보건") || title.trim().toLowerCase().contains("의약")
						|| title.trim().toLowerCase().contains("음식") || title.trim().toLowerCase().contains("식품")
						|| title.trim().toLowerCase().contains("호흡") || title.trim().toLowerCase().contains("스트레스")
						|| title.trim().toLowerCase().contains("수명") || title.trim().toLowerCase().contains("키")
						|| title.trim().toLowerCase().contains("제약") || title.trim().toLowerCase().contains("피트니스")
						|| title.trim().toLowerCase().contains("헬스") || title.trim().toLowerCase().contains("질병")
						|| title.trim().toLowerCase().contains("병원")|| title.trim().toLowerCase().contains("관리")
						|| title.trim().toLowerCase().contains("ai")){
					System.out.println("제목: " + title);
					System.out.println("이미지 링크: " + imageUrl);
					System.out.println("기사 링크: " + articleLink);
					System.out.println("----------------------------");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
