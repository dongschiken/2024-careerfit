package com.peach.careerfit.file.component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStorageComponent {

	private static final int maxSize = 5 * 1024 * 1024;

	/**
	 * 파일 유효성 검사
	 * 
	 * @param file
	 */
	public void validateFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
		}
		if (file.getSize() > maxSize) {
			throw new IllegalArgumentException("파일 크기가 너무 큽니다. 최대 5MB 이하의 파일만 업로드할 수 있습니다.");
		}
		if (file.getOriginalFilename().length() < 0) {
			throw new IllegalArgumentException("파일의 실제 이름이 없습니다.");
		}
	}

	/**
	 * type에 따른 이미지 저장소를 따로 구분해서 파일 저장
	 * 
	 * @param file
	 * @param type
	 * @return
	 */
	public String saveFile(MultipartFile file, String type) {
		if (file != null) {
			validateFile(file);
			try {
				String originName = file.getOriginalFilename();
				String subDir = new SimpleDateFormat("yyyy/MM/dd/HH/").format(new Date()).toString();
				File dir = new File("c:/uploads/" + type + "/" + subDir);
				dir.mkdirs();
				String systemName = UUID.randomUUID().toString() + originName;
				File f = new File(dir, systemName);
				file.transferTo(f);
				return dir.toString() + systemName;
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * dto class 타입을 받아서 여러 파일을 저장하는 메서드
	 * 제일 처음 들어오는 IMG파일이 MAIN 이미지
	 * @param <T>
	 * @param files
	 * @param id
	 * @param dtoClass
	 * @param type
	 * @return
	 */
	public <T> List<T> saveFiles(MultipartFile[] files, int id, Class<T> dtoClass, String type) {
		List<T> list = new ArrayList<>();
		String subDir = new SimpleDateFormat("yyyy/MM/dd/HH/").format(new Date()).toString();
		File dir = new File("c:/uploads/" + type + "/" + subDir);
		int count = 0;
		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				try {
					// 파일 저장
					String originName = file.getOriginalFilename();
					String systemName = UUID.randomUUID().toString() + "_" + originName;
					String fullPath = dir + systemName;

					file.transferTo(new File(fullPath)); // 파일 저장 수행

					// 제네릭 타입의 DTO 객체 생성 및 값 설정
					T dtoInstance = dtoClass.getDeclaredConstructor().newInstance();

					// 리플렉션을 통해 필드에 값을 설정
					dtoClass.getMethod("set" + type + "Id", int.class).invoke(dtoInstance, id);
					dtoClass.getMethod("setPath", String.class).invoke(dtoInstance, fullPath);
					dtoClass.getMethod("setSystemName", String.class).invoke(dtoInstance, systemName);
					dtoClass.getMethod("setOriginName", String.class).invoke(dtoInstance, originName);
					if(count < 1) {
						dtoClass.getMethod("setMainWhether", String.class).invoke(dtoInstance, "M");						
					}else {
						dtoClass.getMethod("setMainWhether", String.class).invoke(dtoInstance, "S");												
					}
					list.add(dtoInstance);
					count++;
				} catch (IOException | ReflectiveOperationException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}
