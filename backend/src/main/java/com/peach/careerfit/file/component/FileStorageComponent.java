package com.peach.careerfit.file.component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStorageComponent {
	
	private static final int maxSize = 5 * 1024 * 1024;
	/**
	 * 파일 유효성 검사
	 * @param file
	 */
	public void validateFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
		}
		if (file.getSize() > maxSize) {
			throw new IllegalArgumentException("파일 크기가 너무 큽니다. 최대 5MB 이하의 파일만 업로드할 수 있습니다.");
		}
	}
	/**
	 * type에 따른 이미지 저장소를 따로 구분
	 * @param file
	 * @param type
	 * @return
	 */
	public String saveFile(MultipartFile file, String type) {
		if (file != null) {
			validateFile(file);
			String originName = file.getOriginalFilename();
			if (!file.isEmpty() && originName.length() > 0) {
				try {
					String subDir = new SimpleDateFormat("yyyy/MM/dd/HH/").format(new Date()).toString();
					File dir = new File("c:/uploads/" + type+ "/" + subDir);
					dir.mkdirs();
					String systemName = UUID.randomUUID().toString() + originName;
					File f = new File(dir, systemName);
					file.transferTo(f);
					return dir.toString() + systemName;
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
}
