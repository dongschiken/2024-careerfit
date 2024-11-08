package com.peach.careerfit.board.model.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.board.model.dao.BoardDao;
import com.peach.careerfit.board.model.dto.Board;
import com.peach.careerfit.user.model.dto.User;
import com.peach.careerfit.user.model.service.UserService;

@Service
public class BoardServiceImpl implements BoardService {
	
    private final BoardDao boardDao;
    private final String uploadDir = "C:/uploads";
    private final UserService userService;
    public BoardServiceImpl(BoardDao boardDao, UserService userService) {
        this.boardDao = boardDao;
        this.userService = userService;
    }

    /**
     * 게시글 1개 생성 시 생성날짜와 최근업데이트 날짜를 동일하게 설정하고, 파일을 업로드.
     */
    @Override
    public int registBoard(Board board, String email, MultipartFile[] files) {
        board.setCreatedAt(LocalDateTime.now());
        board.setUpdatedAt(LocalDateTime.now());
        User user = userService.findUserByEmail(email);
        board.setUserId(user.getUserId());
// 		파일 업로드 처리
//        for (MultipartFile file : files) {
//            if (file != null && !file.isEmpty()) {
//                saveFile(file);
//          }
//      }
        System.out.println(board);
        return boardDao.insertBoard(board);
    }

	@Override
	public List<Board> getBoardList() {
		return boardDao.selectBoardAll();
	}

	@Override
	public Board getBoardById(int boardId) {
		return boardDao.selectBoardById(boardId);
	}
    
    /**
     * 파일을 지정된 경로에 저장하고 파일의 경로와 이름을 관리
     * @param file 업로드할 파일
     */
    private void saveFile(MultipartFile file) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        
        // 파일 저장 경로 및 이름 설정
        String systemName = uuid + originalFilename;
        String filePath = uploadDir + "/" + datePath + "/" + systemName;
        
        // 디렉토리 생성
        File dir = new File(uploadDir + "/" + datePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 파일 저장
        File newFile = new File(filePath);
        try {
            file.transferTo(newFile);
        } catch (IOException e) {
            e.printStackTrace();
            // 필요한 경우 예외 처리 로직 추가
        }
    }

}
