package com.example.app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.app.domain.User;
import com.example.app.mapper.UserMapper;
import com.example.app.validation.AddUserGroup;
import com.example.app.validation.LoginGroup;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserMapper userMapper;

	//すべてのリクエストでユーザー情報をセッションから取得
	@ModelAttribute("user")
	public User getUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		User loggedInUser = (User) session.getAttribute("user");
		return loggedInUser != null ? loggedInUser : new User();//ログインしていない場合
	}

	//ログインページ
	@GetMapping("/login")
	public String loginPage(Model model) {
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
			HttpServletRequest request) {
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
		} //homeは
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
		} //VoteControllerのhomeにリダイレクト
	}

	//ユーザーの更新ページ表示
	@GetMapping("/updateUser")
	public String updateUserPage(Model model, HttpServletRequest request) {
		User loggedInUser = (User) request.getSession().getAttribute("user");

		if (loggedInUser == null) {
			return "redirect:/login";
		}

		model.addAttribute("user", loggedInUser);
		return "updateUser";
	}

	//ユーザーの更新・削除
	@PostMapping("/updateUser")
	public String updateUser(
			@Validated(AddUserGroup.class) User user,
			Errors errors,
			Model model,
			HttpServletRequest request,
			@RequestParam(required = false) String action) {

		//セッションからユーザー情報取得
		User loggedInUser = (User) request.getSession().getAttribute("user");

		if (loggedInUser == null) {
			return "redirect:/login";
		}

		//削除ボタンが押された場合
		if ("delete".equals(action)) {
			userMapper.deleteUser(loggedInUser.getUserId());

			//セッションを無効か
			request.getSession().invalidate();
			return "redirect:/login";//削除-＞ログインページへ
		}

		//更新ボタンが押された場合
		if ("update".equals(action)) {
			// 入力エラーがあれば、エラーメッセージを表示
			if (errors.hasErrors()) {
				List<ObjectError> objList = errors.getAllErrors();
				for (ObjectError obj : objList) {
					System.out.println(obj.toString());
				}
				model.addAttribute("user", loggedInUser);// フォームに現在のデータを保持
				return "updateUser";// エラーがあれば、更新ページを再表示
			} else {
				//ユーザー情報を更新
				user.setUserId(loggedInUser.getUserId());// セッションのユーザーIDを更新対象に設定
				userMapper.updateUser(user);//DBへ保存

				//更新後にセッション情報も更新
				request.getSession().setAttribute("user", user);
				model.addAttribute("message", "ユーザー情報を更新しました");
				return "redirect:/home";// 更新後はホーム画面へリダイレクト
			}
		}
		return "redirect:/home"; // アクションが不明な場合、ホームページへリダイレクト
	}
}