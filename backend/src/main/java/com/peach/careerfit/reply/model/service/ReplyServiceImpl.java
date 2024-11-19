package com.peach.careerfit.reply.model.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.peach.careerfit.reply.model.dao.ReplyDao;
import com.peach.careerfit.reply.model.dto.Reply;
import com.peach.careerfit.reply.model.dto.ResponseReply;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReplyServiceImpl implements ReplyService{
	
	private final ReplyDao replyDao;
	
	@Override
	public List<ResponseReply> getReply(int boardId) {
		List<ResponseReply> parents = replyDao.selectReplyByBoardId(boardId);
		List<ResponseReply> childs = replyDao.selectReplyChildByParentId(boardId);
		System.out.println(parents);
		System.out.println(childs);
		for (int i = 0; i < parents.size(); i++) {
			List<ResponseReply> child = new ArrayList<>();
			for (int j = 0; j < childs.size(); j++) {
				if(parents.get(i).getReplyId() == childs.get(j).getParentReplyId()) {
					child.add(childs.get(j));
				}
			}
			parents.get(i).setReplyResponses(child);
		}
		return parents;
	}

	@Override
	public int registReply(Reply reply) {
		System.out.println(reply);
		reply.setCreatedAt(LocalDateTime.now().minusSeconds(1));
		reply.setUpdatedAt(LocalDateTime.now().minusSeconds(1));
		int status = replyDao.insertReply(reply);
		return status;
	}

	@Override
	public int setReply(Reply reply) {
		
		return 0;
	}

	@Override
	public int deleteReply(int replyId) {
		return replyDao.deleteReplyByReplyId(replyId);
	}

}