package com.peach.careerfit.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GptConfig {
	
	@Value("${openai.api.key}")
	private String openaiApiKey;
	
	@Bean
	@Qualifier("openapiRestTemplate")
	public RestTemplate openapiRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
			request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
			request.getHeaders().add("Content-Type", "application/json");
			return execution.execute(request, body);
		};
		 // RestTemplate에 인터셉터 등록
        restTemplate.setInterceptors(Collections.singletonList(interceptor));
        return restTemplate;
	}
}
