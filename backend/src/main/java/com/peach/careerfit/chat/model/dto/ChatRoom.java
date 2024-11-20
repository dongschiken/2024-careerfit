package com.peach.careerfit.chat.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {
    private int chatRoomId;        // 채팅방 ID
    private String title;          // 채팅방 이름
    private Timestamp createdAt;   // 생성 시간
    private Timestamp updatedAt;   // 마지막 수정 시간
    private Timestamp lastAt;      // 마지막 메시지 시간
    private String creatorNickname;
    private String creatorProfile;
}
