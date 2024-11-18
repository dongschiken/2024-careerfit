package com.peach.careerfit.user.model.dto;

import jakarta.annotation.Generated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTokenUser {
	private int userId;
	private String email;
	private String nickname;
	private String role;
}
