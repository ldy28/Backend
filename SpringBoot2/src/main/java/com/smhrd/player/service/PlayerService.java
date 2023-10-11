package com.smhrd.player.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.smhrd.player.converter.ImageToBase64;
import com.smhrd.player.converter.PlayerImageConverter;
import com.smhrd.player.entity.Player;
import com.smhrd.player.mapper.PlayerMapper;

@Service
public class PlayerService {

	@Autowired
	private PlayerMapper playerMapper;

	@Autowired
	private ResourceLoader resourceLoader; // 특정 경로에 있는 파일을 읽어오기 위한 객체

	public JSONArray PlayerList() {
		List<Player> p_list = playerMapper.PlayerList();

		// DB에서 가지고 온 선수의 정보 중에서 imgSrc는 실제경로, 실제 파일이 아님!
		// 리액트 서버에 이미지를 직접 넘겨야하기 때문에 이미지 데이터 가공해야 함
		// imgSrc 파일이름 -> 실제 파일 -> 이미지(byte형태의 문자열)

		PlayerImageConverter<File, String> converter = new ImageToBase64();

		JSONArray jsonPlayerList = new JSONArray();

		for (Player p : p_list) {
			// 1. 파일이름 -> 실제 파일 -> byte문자열
			// 1-1. 파일이름
//			String filePath = "classpath:/static/player_img/" + p.getImgSrc();
			String filePath = "static/player_img/" + p.getImgSrc();

			// 1-2. 실제 파일로 불러오기
			Resource resource = resourceLoader.getResource(filePath);

			// 1-3. converter를 통해서 byte문자열로 변환

			File TempFile = null;
			String fileString = null;
			try {
				InputStream inputStream = new ClassPathResource(filePath).getInputStream();
				TempFile = File.createTempFile(p.getImgSrc(), "");
				FileUtils.copyInputStreamToFile(inputStream, TempFile);
				// 1-3. converter를 통해서 byte문자열로 변환
				fileString = converter.converte(TempFile);
			} catch (IOException e) {
				e.printStackTrace();
			}

			/* System.out.println("fileString : " + fileString); */

			// 2. imgSrc 필드 : 원래 파일 이름 -> byte문자열
			p.setImgSrc(fileString);

			// 3. Json형태로 변환(JsonArray)

			jsonPlayerList.add(p);

		}

		return jsonPlayerList;

	}

	public JSONObject playerDetail(String name) {
		Player p = playerMapper.playerDetail(name);

		PlayerImageConverter<File, String> converter = new ImageToBase64();
		JSONObject jsonplayer = new JSONObject();

//		String filePath = "classpath:/static/player_img/" + p.getImgSrc();
		String filePath = "static/player_img/" + p.getImgSrc();

		// 1-2. 실제 파일로 불러오기
		Resource resource = resourceLoader.getResource(filePath);

		// 1-3. converter를 통해서 byte문자열로 변환

		File TempFile = null;
		String fileString = null;

		try {
			// 배포 시 파일로 불러오면 파일을 못찾기 때문에 inputStream으로 진행
			InputStream inputStream = new ClassPathResource(filePath).getInputStream();
			TempFile = File.createTempFile(p.getImgSrc(), "");
			FileUtils.copyInputStreamToFile(inputStream, TempFile);
			// 1-3. converter를 통해서 byte문자열로 변환
			fileString = converter.converte(TempFile);

		} catch (IOException e) {
			e.printStackTrace();
		}

		/* System.out.println("fileString : " + fileString); */

		// 2. imgSrc 필드 : 원래 파일 이름 -> byte문자열
		p.setImgSrc(fileString);

		jsonplayer.put("jsonPlayer", p);

		return jsonplayer;
	}

}
