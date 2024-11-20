package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.app.domain.User;
import com.example.app.mapper.UserMapper;

import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserMapper userMapper;
	
	
	//新規ユーザー登録ページを表示
	@GetMapping("/addUser")
	public String addUserGet(Model model) {
		model.addAttribute("user", new User());//空のUserオブジェクト
		return "addUser";
	}
	
	//新規ユーザー登録
	@PostMapping("/adduser")
	public String adduserPost(User user, Model model) {
		//新規追加処理
		userMapper.addUser(user);
		model.addAttribute("message", "ユーザー登録完了");
		
		return "login";//登録完了後はログインページに遷移
	}
	
	

	
	
	//ユーザー追加

	//ユーザーの削除
}
