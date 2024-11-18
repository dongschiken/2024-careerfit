package com.peach.careerfit.reply.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peach.careerfit.jwt.JwtUtils;
import com.peach.careerfit.reply.model.dto.Reply;
import com.peach.careerfit.reply.model.dto.ReplyResponse;
import com.peach.careerfit.reply.model.service.ReplyService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reply")
public class ReplyController {
	
	private final JwtUtils jwtUtils;
	private final ReplyService replyService;
	
	@GetMapping("/{boardId}") 
	public ResponseEntity<Object> getReplies(@PathVariable("boardId") int boardId) {
		List<ReplyResponse> replies = replyService.getReply(boardId);
		try {
			if(replies.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("댓글이 없습니다.");
			}else {
				return ResponseEntity.status(HttpStatus.OK).body(replies);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 불러오는 중 오류 발생");
		}
	}
	
	@PostMapping
	public ResponseEntity<Object> registReply(Reply reply) {
		try {
			int status = replyService.registReply(reply);
			if(status == 0) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 등록중 오류 발생");
			}else {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("댓글 등록에 성공했습니다.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 등록중 오류 발생");
		}
	}
	
	@PutMapping
	public ResponseEntity<Object> setReply(Reply reply) {
		try {
			int status = replyService.setReply(reply);
			if(status == 0) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정중 오류 발생");
			}else {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("댓글 수정에 성공했습니다.")
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정중 오류 발생");
		}
	}
	
	@DeleteMapping("/{replyId}")
	public ResponseEntity<Object> deleteReply(@PathVariable("replyId") int replyId) {
		try {
			int status = replyService.deleteReply(replyId);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
