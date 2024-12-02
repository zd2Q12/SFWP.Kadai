package com.example.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.app.domain.User;
import com.example.app.domain.VoteItem;
import com.example.app.domain.VoteResult;
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
  public User getUser(HttpServletRequest request,Model model) {
      HttpSession session = request.getSession();
      User loggedInUser = (User) session.getAttribute("user");
      
      // ログインしていない場合、カスタム例外をスローしてリダイレクトさせる
      if (loggedInUser == null) {
      	throw new RedirectToLoginException();
      }
    	//ユーザー情報をモデルに追加・表示
  			model.addAttribute("userName", loggedInUser.getUserName());
  			model.addAttribute("userId", loggedInUser.getUserId());
  	

      return loggedInUser; // ログインしている場合はそのユーザー情報を返す
  }

  // ログインしていない場合にリダイレクトさせるための例外クラス
  public static class RedirectToLoginException extends RuntimeException {}

  // 例外ハンドラーでリダイレクト処理
  @ExceptionHandler(RedirectToLoginException.class)
  public String handleRedirectToLogin() {
      return "redirect:/login"; // ログイン画面へ遷移
    }
  
	
	//Homeページへ遷移
	@GetMapping("/home")
	public String homePage(Model model) {
    // セッションから取得したユーザー情報を自動的にモデルに追加
		User user = (User) model.getAttribute("user");

		//ユーザー情報をモデルに追加・表示
		if (user != null) {
			model.addAttribute("userName", user.getUserName());
			model.addAttribute("userId", user.getUserId());
		}

		//全ての投票を取得（他のユーザーの投稿を表示）
		List<VoteItem> voteItems = voteItemmapper.selectAll();
		// 投票アイテムのリストをビューに渡す
		model.addAttribute("voteItems", voteItems);
		//新規投票作成用フォーム
		model.addAttribute("voteItem", new VoteItem());
		
		//投票期間が終了したかを判定、モデルに情報を渡す
		LocalDate today = LocalDate.now();
		model.addAttribute("today", today);

		return "home";
	}

	//投票の新規作成・変更更新・削除
	@PostMapping("/home")
	public String createVoteItem(
			@RequestParam("action") String action,
			VoteItem voteItem,
			@RequestParam(value = "id", required = false) Integer id,
			@ModelAttribute("user")User user) {

		// ユーザーがセッションにいない場合
		if (user == null) {
			return "redirect:/login"; // ログインしていなければリダイレクト
		}

		// セッションから取得したUserオブジェクトからユーザーIDを取得
		Integer userId = user.getUserId();

		// action に基づいて処理を分岐
		if ("create".equals(action)) {
			//新規作成
			voteItem.setCreatedBy(userId);
			voteItemmapper.addVoteItem(voteItem);
		} else if ("update".equals(action)) {
			// 更新
			voteItem.setVoteItemId(id);//対象のIDセット
			voteItemmapper.updateVoteItem(voteItem);
		} else if ("delete".equals(action) && id != null) {
			//削除
			voteItemmapper.deleteVoteItem(id);
		}
		return "redirect:/home";
	}

	//投票結果の追加処理
	@PostMapping("/vote")
	public String vote(
			@RequestParam("voteItemId") Integer voteItemId,
			@RequestParam("voteValue") Integer voteValue,
			@ModelAttribute("user")User user,
			Model model) {

		if (user == null) {
      model.addAttribute("errorMessage", "ログインしていないため投票できません。");
			return "redirect:/login";//ログインしてなかったらリダイレクト
		}
		
    // すでに投票しているかチェック
    VoteResult existingVote = voteItemmapper.findVoteResultByUserIdAndVoteItemId(user.getUserId(), voteItemId);
    if (existingVote != null) {
        // 投票済みの場合、エラーメッセージを渡す
        model.addAttribute("errorMessage", "すでに投票済みです。");
        // 投票アイテムのリストを再取得
        List<VoteItem> voteItems = voteItemmapper.selectAll();
        model.addAttribute("voteItems", voteItems);
        return "home"; // ホーム画面に戻る
    }

		// VoteResult オブジェクトを作成して、投票結果を保存(投票結果を新規に追加)
		VoteResult voteResult = new VoteResult();
		voteResult.setVoteItemId(voteItemId);
		voteResult.setUserId(user.getUserId());
		voteResult.setVoteValue(voteValue);


    // 投票結果をデータベースに挿入
		//重複した投票防止
    try {
        voteItemmapper.addVoteResult(voteResult);  // ここで重複チェックが行われる
    } catch (DuplicateKeyException e) {
        // 重複エラーが発生した場合、エラーメッセージを表示
        model.addAttribute("errorMessage", "すでに投票済みです。");
        List<VoteItem> voteItems = voteItemmapper.selectAll();
        model.addAttribute("voteItems", voteItems);
        return "home"; // ホーム画面に戻る
    }

		//投票後に、賛成・反対票数を更新
		voteItemmapper.updateVoteCount(voteItemId);

		return "redirect:/home"; // 投票済みならそのままリダイレクト
	}

}
