package com.peach.careerfit.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peach.careerfit.chat.model.dto.ChatParticipantResponse;
import com.peach.careerfit.chat.model.dto.ChatRoomRequest;
import com.peach.careerfit.chat.model.dto.ChatRoomUserRequest;
import com.peach.careerfit.chat.model.service.ChatRoomService;

@RestController
@RequestMapping("/api")
public class ChatRoomController {

	@Autowired
	private ChatRoomService chatRoomService;

	// 채팅방 생성
	@PostMapping("/chat-room")
	public ResponseEntity<String> createChatRoom(@RequestBody ChatRoomRequest request) {
	    try {
	        // 서비스 계층으로 전달된 ChatRoomRequest 객체의 title 필드가 null인지 확인
	        if (request.getTitle() == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("채팅방 제목은 필수입니다.");
	        }
	        
	        chatRoomService.createChatRoom(request);
	        return ResponseEntity.status(HttpStatus.CREATED).body("채팅방이 성공적으로 생성되었습니다.");
	    } catch (Exception e) {
	        e.printStackTrace();  // 예외 메시지를 콘솔에 출력
	        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("채팅방 생성 중 오류가 발생했습니다.");
	    }
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
    public ResponseEntity<String> joinChatRoom(@PathVariable("chat_room_id") int chatRoomId, @RequestBody ChatRoomUserRequest request){
    	try {
    		boolean alreadyJoined = chatRoomService.isUserAlreadyInChatRoom(chatRoomId, request.getUserId());
    		if(alreadyJoined) {
    			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미 채팅방에 참여한 사용자입니다.");
    		}
    		chatRoomService.joinChatRoom(chatRoomId, request);
    		return ResponseEntity.status(HttpStatus.CREATED).body("채팅방에 성공적으로 참여했습니다.");
    	} catch (Exception e) {
    		e.printStackTrace();
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("채팅방 참여 중 오류가 발생했습니다.");
		}
    }
    
    // 채팅방 나가기
    @DeleteMapping("/chat-room/{chat_room_id}/users/{user_id}")
    public ResponseEntity<String> leaveChatRoom(@PathVariable("chat_room_id") int chatRoomId, @PathVariable("user_id") int userId) {
    	try {
    		boolean isInChat = chatRoomService.isUserAlreadyInChatRoom(chatRoomId, userId);
    		if(!isInChat) {
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
    public ResponseEntity<List<ChatParticipantResponse>> getParticipants(@PathVariable("chat_room_id") int chatRoomid, @RequestParam(value = "page", required = false, defaultValue = "0") int page, @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
    	try {
    		List<ChatParticipantResponse> participants = chatRoomService.getParticipants(chatRoomid, page, size);
    		if(participants.isEmpty()) {
    			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    		}
    		return ResponseEntity.ok(participants);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
    }
}
