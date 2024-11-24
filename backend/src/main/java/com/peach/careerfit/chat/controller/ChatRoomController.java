package com.peach.careerfit.chat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peach.careerfit.chat.model.dto.ChatParticipantResponse;
import com.peach.careerfit.chat.model.dto.ChatRoom;
import com.peach.careerfit.chat.model.dto.ChatRoomRequest;
import com.peach.careerfit.chat.model.dto.ChatRoomUserRequest;
import com.peach.careerfit.chat.model.service.ChatRoomService;
import com.peach.careerfit.jwt.JwtResponse;
import com.peach.careerfit.jwt.JwtUtils;
import com.peach.careerfit.user.model.dto.ResponseTokenUser;
import com.peach.careerfit.user.model.dto.User;
import com.peach.careerfit.user.model.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatRoomController {

	private final ChatRoomService chatRoomService;
	private final UserService userService;
	private final JwtResponse jwtResponse;
	private final JwtUtils jwtUtils;
	

	// 채팅방 생성
	@PostMapping("/chat-room")
	public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoomRequest chatRoomRequest, HttpServletRequest request) {
	   System.out.println("호출");
		
		// 로그인된 사용자 정보
		String token = jwtUtils.getAccessToken(request);
		User user = userService.getUserById(jwtUtils.getUserIdFromToken(token));

	    // 요청에 로그인된 사용자 정보를 추가
	    chatRoomRequest.setUserId(user.getUserId());
	    chatRoomRequest.setUserNickname(user.getNickname());


	    // 프로필이 NULL일 경우 기본값 설정
	    if (user.getProfileUrl() == null || user.getProfileUrl().isEmpty()) {
	        chatRoomRequest.setUserProfile("/default-profile.png");
	    } else {
	        chatRoomRequest.setUserProfile(user.getProfileUrl());
	    }
	    
	    // 서비스 호출
	    ChatRoom createdChatRoom = chatRoomService.createChatRoom(chatRoomRequest);

	    return ResponseEntity.ok(createdChatRoom);
		

	}

	// 채팅방 삭제
	@DeleteMapping("/chat-room/{chat_room_id}")
	public ResponseEntity<String> deleteChatRoom(@PathVariable("chat_room_id") int chatRoomId) {
		try {
			chatRoomService.deleteChatRoom(chatRoomId);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("채팅방 삭제 중 오류가 발생했습니다.");
		}
	}

	// 채팅방 참여
	@PostMapping("/chat-room/{chat_room_id}/users")
	public ResponseEntity<String> joinChatRoom(
	    @PathVariable("chat_room_id") int chatRoomId,
	    HttpServletRequest request) {
		System.out.println(chatRoomId+"에 들어옴");
		ResponseTokenUser user = jwtResponse.extractTokenUser(request);
	    if (user == null) {
	    	System.out.println("유저가 없음");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	    }
	    try {
	    	System.out.println(user.getUserId());
	        boolean alreadyJoined = chatRoomService.isUserAlreadyInChatRoom(chatRoomId, user.getUserId());
	        if (alreadyJoined) {
	            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 참여한 채팅방입니다.");
	        }
	        chatRoomService.joinChatRoom(chatRoomId, new ChatRoomUserRequest(user.getUserId()));
	        return ResponseEntity.status(HttpStatus.CREATED).body("채팅방 참여 성공");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("채팅방 참여 중 오류 발생");
	    }
	}



	// 채팅방 나가기
	@DeleteMapping("/chat-room/{chat_room_id}/users/{user_id}")
	public ResponseEntity<String> leaveChatRoom(@PathVariable("chat_room_id") int chatRoomId,
			@PathVariable("user_id") int userId) {
		try {
			boolean isInChat = chatRoomService.isUserAlreadyInChatRoom(chatRoomId, userId);
			if (!isInChat) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 사용자는 채팅방에 없습니다.");
			}
			chatRoomService.leaveChatRoom(chatRoomId, userId);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("채팅방 나가기 중 오류가 발생했습니다.");
		}
	}

	// 참여자 목록 조회
	@GetMapping("/chat-room/{chat_room_id}/users")
	public ResponseEntity<List<ChatParticipantResponse>> getParticipants(@PathVariable("chat_room_id") int chatRoomid,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "10") int size) {
		try {
			List<ChatParticipantResponse> participants = chatRoomService.getParticipants(chatRoomid, page, size);
			if (participants.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
			}
			return ResponseEntity.ok(participants);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/chat-room/{chat_room_id}")
	public ResponseEntity<ChatRoom> getChatRoomInfo(@PathVariable("chat_room_id") int chatRoomId) {
		try {
			// ChatRoomService에서 데이터를 조회
			System.out.println(chatRoomId);
			ChatRoom chatRoom = chatRoomService.getChatRoomById(chatRoomId);
			// 채팅방이 존재하지 않는 경우 처리
			if (chatRoom == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			// 성공적으로 채팅방 정보를 반환
			return ResponseEntity.ok(chatRoom);
		} catch (Exception e) {
			// 예외 처리
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 모든 채팅방 조회
	@GetMapping("/chat-room")
	public ResponseEntity<List<ChatRoom>> getAllChatRooms() {
		List<ChatRoom> chatRooms = chatRoomService.getAllChatRooms();
		return ResponseEntity.ok(chatRooms);
	}

	// 해당 장소의 채팅 리스트 조회
	@GetMapping("/chat-rooms")
	public ResponseEntity<List<ChatRoom>> getChatRoomsByPlaceId(@RequestParam("placeId") int placeId) {
	    try {
	        List<ChatRoom> chatRooms = chatRoomService.getChatRoomsByPlaceId(placeId);
	        return ResponseEntity.ok(chatRooms);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	
	// ChatRoomController.java
	@GetMapping("/chat-room/my")
	public ResponseEntity<List<ChatRoom>> getMyChatRooms(HttpServletRequest request) {
	    try {
	        ResponseTokenUser user = jwtResponse.extractTokenUser(request);
	        if (user == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	        }
	        List<ChatRoom> myChatRooms = chatRoomService.getMyChatRooms(user.getUserId());
	        return ResponseEntity.ok(myChatRooms);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}


}
