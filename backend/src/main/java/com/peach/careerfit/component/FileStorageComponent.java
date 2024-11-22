package com.peach.careerfit.component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
		if (file != null && file.getOriginalFilename().length() < 0) {
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
				String path = dir.toString();
				String systemName = "/"+UUID.randomUUID().toString() + "_" + originName;
				String relativePath = path.replace("c:\\uploads\\", "").replace("\\", "/");
				file.transferTo(new File(path, systemName));
				return relativePath + systemName;
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * dto class 타입을 받아서 여러 파일을 저장하는 메서드 제일 처음 들어오는 IMG파일이 MAIN 이미지
	 * 
	 * @param <T>
	 * @param files
	 * @param id
	 * @param dtoClass
	 * @param type
	 * @return
	 */
	public <T> List<T> saveFiles(List<MultipartFile> files, int id, Class<T> dtoClass, String type) {
		List<T> list = new ArrayList<>();
		String subDir = new SimpleDateFormat("yyyy/MM/dd/HH/").format(new Date()).toString();
		File dir = new File("c:/uploads/" + type + "/" + subDir);
		dir.mkdirs(); 
		try {
			Method setIdMethod = dtoClass.getMethod("set" + type + "Id", int.class);
			Method setPathMethod = dtoClass.getMethod("setPath", String.class);
			Method setSystemNameMethod = dtoClass.getMethod("setSystemName", String.class);
			Method setOriginNameMethod = dtoClass.getMethod("setOriginName", String.class);
			Method setMainWhetherMethod = dtoClass.getMethod("setMainWhether", String.class);

			int count = 0;
			for (MultipartFile file : Optional.ofNullable(files).orElse(java.util.Collections.emptyList())) {
				if (!file.isEmpty()) {
					// 파일 저장
					String originName = file.getOriginalFilename();
					String systemName = "/"+UUID.randomUUID().toString() + "_" + originName;
					String path = dir.toString();
					file.transferTo(new File(path, systemName));
					
					T dtoInstance = dtoClass.getDeclaredConstructor().newInstance();
					String relativePath = path.replace("c:\\uploads\\", "").replace("\\", "/");
					setIdMethod.invoke(dtoInstance, id);
					setPathMethod.invoke(dtoInstance, relativePath);
					setSystemNameMethod.invoke(dtoInstance, systemName);
					setOriginNameMethod.invoke(dtoInstance, originName);
					setMainWhetherMethod.invoke(dtoInstance, count++ == 0 ? "M" : "S");	
					list.add(dtoInstance);
				}
			}
		} catch (IOException | ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return list;
	}
}
