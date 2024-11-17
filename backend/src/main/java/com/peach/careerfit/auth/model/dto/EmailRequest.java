package com.peach.careerfit.auth.model.dto;

public class EmailRequest {
    private String email;

    // 기본 생성자
    public EmailRequest() {}

    // 생성자
    public EmailRequest(String email) {
        this.email = email;
    }

    // Getter와 Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
