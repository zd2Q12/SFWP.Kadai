package com.example.app.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.app.domain.User;
import com.example.app.domain.VoteItem;
import com.example.app.mapper.VoteItemMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VoteItemController {

	private final VoteItemMapper voteItemmapper;

	// すべてのリクエストで共通のユーザー情報を取得
	@ModelAttribute("user")
	public User getUser(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		User loggedInUser = (User) session.getAttribute("user");

		// ログインしていない場合、カスタム例外をスローしてリダイレクトさせる
		if (loggedInUser == null) {
			System.out.println("ユーザーがログインしていません");
			throw new RedirectToLoginException();
		}
		//ユーザー情報をモデルに追加・表示
		model.addAttribute("userName", loggedInUser.getUserName());
		model.addAttribute("userId", loggedInUser.getUserId());
System.out.println("ログインユーザー：" + loggedInUser.getUserName());
		return loggedInUser; // ログインしている場合はそのユーザー情報を返す
	}

	// ログインしていない場合にリダイレクトさせるための例外クラス
	public static class RedirectToLoginException extends RuntimeException {
	}

	// 例外ハンドラーでリダイレクト処理
	@ExceptionHandler(RedirectToLoginException.class)
	public String handleRedirectToLogin() {
		return "redirect:/login"; // ログイン画面へ遷移
	}

	//Homeページへ遷移
	@GetMapping("/home")
	public String homePage(
			@RequestParam(value = "sortBy", defaultValue = "dateDesc") String sortBy,
			Model model) {
		// セッションから取得したユーザー情報を自動的にモデルに追加
		User user = (User) model.getAttribute("user");

		//ユーザー情報をモデルに追加・表示
		if (user != null) {
			model.addAttribute("userName", user.getUserName());
			model.addAttribute("userId", user.getUserId());
		}

		//全ての投票を取得（並び替え条件に応じて）
		List<VoteItem> voteItems = new ArrayList<>();
		switch (sortBy) {
		case "dateAsc":
			voteItems = voteItemmapper.selectAllOrderByDateAsc();
			break;
		case "myPosts":
			voteItems = voteItemmapper.selectByUserId(user.getUserId());
			break;
		case "dateDesc":
		default:
			voteItems = voteItemmapper.selectAllOrderByDateDesc();
			break;
		}

		// 投票アイテムのリストをビューに渡す
		model.addAttribute("voteItems", voteItems);

		//新規投票作成用フォーム
		model.addAttribute("voteItem", new VoteItem());

		//投票期間が終了したかを判定、モデルに情報を渡す
		LocalDateTime today = LocalDateTime.now();
		model.addAttribute("today", today);

		//プルダウンの選択状態を保持
		model.addAttribute("sortBy", sortBy);

		return "home";
	}

	//投票の新規作成・変更更新・削除
	@PostMapping("/home")
	public String createVoteItem(
			@RequestParam("action") String action,
			VoteItem voteItem,
			@RequestParam(value = "id", required = false) Integer id,
			@ModelAttribute("user") User user) {

		// ユーザーがセッションにいない場合
		if (user == null) {
      System.out.println("User is not logged in.");
			return "redirect:/login"; // ログインしていなければリダイレクト
		}

		// セッションから取得したUserオブジェクトからユーザーIDを取得
		Integer userId = user.getUserId();
		System.out.println("User ID:" + userId);

		// action に基づいて新規投稿作成
		if ("create".equals(action)) {
			//新規作成
			voteItem.setCreatedBy(userId);
			voteItemmapper.addVoteItem(voteItem);
			System.out.println("新規投票作成：" + voteItem.getTitle());
		} 
		return "redirect:/home";
	}

	// 更新画面への遷移（新規追加）
	@GetMapping("/update/{id}")
	public String updateVoteItemForm(@PathVariable Integer id, Model model) {
		// 対象の投票アイテムを取得
		VoteItem voteItem = voteItemmapper.getVoteItemById(id);
		model.addAttribute("voteItem", voteItem);
		return "updateVoteItem"; // 更新用のビュー
	}

	// 更新処理（新規追加）
	@PostMapping("/update/{id}")
	public String updateVoteItem(
			@PathVariable Integer id,
			@ModelAttribute VoteItem voteItem,
			@ModelAttribute("user") User user) {
		// ユーザーIDをセットして更新
		voteItem.setVoteItemId(id);
		voteItem.setCreatedBy(user.getUserId());
		voteItemmapper.updateVoteItem(voteItem);
		return "redirect:/home"; // 更新後はホームにリダイレクト
	}

	// 削除確認画面への遷移（新規追加）
	@GetMapping("/delete/{voteItemId}")
	public String deleteVoteItemConfirmation(@PathVariable Integer voteItemId, Model model) {
		// 対象の投票アイテムを取得
		VoteItem voteItem = voteItemmapper.getVoteItemById(voteItemId);
		if(voteItem == null) {
			System.out.println("投稿が見つかりません：" + voteItemId);
			return "home";
		}
		//削除するvoteItemの行を全て取り出し、nullの箇所を調べる↓
		//voteItemIdがnullだったのでSQLクエリを確認したら、getVoteItemByIdにvoteItemIdを
		//取得するクエリが存在しなかった。-＞1週間程悩んでいたことを、質問したら5分で解決
		System.out.println("voteItem->"+voteItem);
		model.addAttribute("voteItem", voteItem);
		System.out.println("削除する投稿の取得：" + voteItemId);
		return "deleteVoteItem"; // 削除確認用のビュー
	}

	// 削除処理（新規追加）
	@PostMapping("/delete/{voteItemId}")
	public String deleteVoteItem(@PathVariable Integer voteItemId,
			RedirectAttributes rd) {
		System.out.println("削除完了する投稿の取得：" + voteItemId);
		voteItemmapper.deleteVoteItem(voteItemId);
		rd.addFlashAttribute("statusMessage", "投稿を削除しました。");
		return "redirect:/home"; // 削除後はホームにリダイレクト
	}

}
