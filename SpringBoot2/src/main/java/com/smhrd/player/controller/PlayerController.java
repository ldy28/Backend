package com.smhrd.player.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smhrd.player.service.PlayerService;

@RestController // 리액트 서버로 데이터만 응답
@CrossOrigin("http://15.164.102.254:3000") // aws : 서버주소 변경
public class PlayerController {

	@Autowired
	private PlayerService playerService;

	// select * from player;
	@RequestMapping("/PlayerList.do")
	public JSONArray PlayerList() {
		System.out.println("Spring : PlayerList.do");
		return playerService.PlayerList();
	}

	@RequestMapping("/PlayerDetail.do")
	public JSONObject PlayerDetail(@RequestParam String name) {
		
		// DB에서 해당하는 이름 정보만 가지고 와서 리액트로 전송
		// JsonObject로 반환 -> put메소드로 데이터 담기
		System.out.println("이름 : " + name);
		return playerService.playerDetail(name);
	}
}
