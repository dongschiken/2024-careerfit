package com.peach.careerfit.board.model.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.peach.careerfit.board.model.dao.BoardDao;
import com.peach.careerfit.board.model.dto.Board;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardDao boardDao;
    private final String uploadDir = "C:/uploads";

    public BoardServiceImpl(BoardDao boardDao) {
        this.boardDao = boardDao;
    }

    /**
     * 게시글 1개 생성 시 생성날짜와 최근업데이트 날짜를 동일하게 설정하고, 파일을 업로드.
     */
    @Override
    public int registBoard(Board board, MultipartFile[] files) {
        board.setCreated_at(LocalDateTime.now());
        board.setUpdated_at(LocalDateTime.now());
        
        // 파일 업로드 처리
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                saveFile(file);
            }
        }
        
        return boardDao.insertBoard(board);
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
