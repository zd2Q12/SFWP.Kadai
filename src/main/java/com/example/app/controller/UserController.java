package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.app.domain.User;
import com.example.app.mapper.UserMapper;

import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserMapper userMapper;
	
	//ログインページ
	@GetMapping("/login")
	public String loginPage() {
		return "login";//ログインページへ
	}
	
	//ログイン
	@PostMapping("/login")
	public String login(
			@RequestParam String userName,
			@RequestParam String password,
			Model model
			) {
		//ユーザー名とパスワードでユーザーを取得
		User user = userMapper.getUserByName(userName);
		
		if(user != null &&user.getPassword().equals(password)) {
      //認証OK-＞セッションに保存
			model.addAttribute("user", user);
			return "redirect://home";//ログインしたらHOMEページへリダイレクト
		}else {
			//認証失敗　エラーメッセージを出す
			model.addAttribute("message", "ユーザー名またはパスワードが間違っています");
			return "login";//ログインページを再表示
		}
	}
	
	//Homeページへ
	@GetMapping("/home")
	public String homePage(Model model) {
		return "home";
	}
	
	
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
