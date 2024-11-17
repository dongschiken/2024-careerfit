package com.peach.careerfit.user.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
	private int userId;
	private String email;
	private String password;
	private String nickname;
	private String profileUrl;
	private String phone;
	private String postCode;
	private String parcelAddress;
	private String streetAddress;
	private String detailAddress;
	private String refreshToken;
	private double latitude;
	private double longitude;
	private LocalDate createdAt;
	private LocalDate updatedAt;
	private int status;
	private String role;
}
