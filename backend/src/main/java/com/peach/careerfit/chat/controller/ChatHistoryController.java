package com.peach.careerfit.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peach.careerfit.chat.model.dto.ChatMessageRequest;
import com.peach.careerfit.chat.model.dto.ChatMessageResponse;
import com.peach.careerfit.chat.model.service.ChatHistoryService;
import com.peach.careerfit.jwt.JwtResponse;
import com.peach.careerfit.user.model.dto.ResponseTokenUser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatHistoryController {

	private final ChatHistoryService chatHistoryService;
	private final JwtResponse jwtResponse;
	
	// 메세지 전송
	@PostMapping("/chat-rooms/{chat_room_id}/history")
	public ResponseEntity<Void> sendMessage(@PathVariable("chat_room_id") int chatRoomId,
			@RequestBody ChatMessageRequest request,
			HttpServletRequest servletRequest) {
		ResponseTokenUser user = jwtResponse.extractTokenUser(servletRequest);
		try {
			System.out.println(chatRoomId + "||||" + user.getUserId());
			chatHistoryService.sendMessage(chatRoomId, user.getUserId(), request);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// 메세지 목록 조회
	@GetMapping("/chat-room/{chat_room_id}/history")
	public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable("chat_room_id") int chatRoomId) {
		List<ChatMessageResponse> messages = chatHistoryService.getMessage(chatRoomId);

		return ResponseEntity.ok(messages);
	}
}
