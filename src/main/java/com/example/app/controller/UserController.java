package com.example.app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.app.domain.User;
import com.example.app.mapper.UserMapper;
import com.example.app.validation.AddUserGroup;
import com.example.app.validation.LoginGroup;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserMapper userMapper;

	
	//Homeページへ
	@GetMapping("/home")
	public String homePage(Model model) {
		//ほかのユーザーの投票の投稿見る、投票する、投票結果見る
		return "home";
	}
	
	@PostMapping("/home")
	public String home() {
		//投票の新規作成・変更更新・削除
		//
		return "";
	}
	
	//ログインページ
	@GetMapping("/login")
	public String loginPage(
			Model model) {
		// 新しいUserオブジェクトをモデルに追加
		model.addAttribute("user", new User());
		return "login";//ログインページへ
	}

	//ログイン
	@PostMapping("/login")
	public String login(
			// Userオブジェクトをフォームから受け取る
			@Validated(LoginGroup.class) User user,
			Errors errors,
			Model model,
			HttpServletRequest request,
			HttpServletResponse response
			) {
		//ユーザー名とパスワードでユーザーを取得
		User foundUser = userMapper.getUserByUserNameAndPassword(user.getUserName(), user.getPassword());
		// 入力エラーがあれば、ログインページに戻る
		if (errors.hasErrors()) {
			//エラー内容の補足
			List<ObjectError> objList = errors.getAllErrors();
			for (ObjectError obj : objList) {
				System.out.println(obj.toString());
			}
			return "login";

		} else {
			//認証OK-＞セッションに保存
				HttpSession session = request.getSession();
				session.setAttribute("user", foundUser);//セッションにユーザー情報を格納
				//model.addAttribute("user", foundUser);
				return "redirect:/home";//ログインしたらHOMEページへリダイレクト
			}//homeは
		}
	



	//新規ユーザー登録ページを表示
	@GetMapping("/addUser")
	public String addUserGet(Model model) {
		//thymeleafの方でドメインクラスを使えるように
		model.addAttribute("user", new User());//空のUserオブジェクト
		return "addUser";
	}

	//新規ユーザー登録
	@PostMapping("/addUser")
	public String addUserPost(
			@Validated(AddUserGroup.class) User user,
			Errors errors,
			Model model) {
		//エラーがあれば、エラーメッセージを表示
		if (errors.hasErrors()) {
			//エラー内容の補足
			List<ObjectError> objList = errors.getAllErrors();
			for (ObjectError obj : objList) {
				System.out.println(obj.toString());
			}
			return "addUser";//エラーがあった場合、登録ページに戻る
		} else {
			//新規追加処理
			userMapper.addUser(user);
			model.addAttribute("message", "ユーザー登録完了");

			return "redirect:/login";//登録完了後はログインページに遷移
		}//VoteControllerのhomeにリダイレクト
	}

	//ユーザーの削除
}
