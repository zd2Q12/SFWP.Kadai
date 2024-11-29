package com.example.app.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

	//Homeページへ遷移
	@GetMapping("/home")
	public String homePage(
			Model model,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

	// デバッグ用に確認
		if (user == null) {
		    System.out.println("User is null, session expired or not set correctly.");
		} else {
		    System.out.println("User session is valid: " + user.getUserName());
		}
		//セッションにユーザー情報があれば、ユーザー情報をモデルに追加・表示
		if (user != null) {
			model.addAttribute("userName", user.getUserName());
			model.addAttribute("userId", user.getUserId());
		}

		//全ての投票を取得（他のユーザーの投稿を表示）
		List<VoteItem> voteItems = voteItemmapper.selectAll();
/*
		// ユーザーが投票済みの情報を取得
		if (user != null) {
			for (VoteItem voteItem : voteItems) {
				//各投票アイテムに対し、ユーザーが投票した結果を取得
				VoteResult existingVote = voteItemmapper.findVoteResultByUserIdAndVoteItemId(user.getUserId(),voteItem.getVoteItemId());
				if (existingVote != null) {
					//投票済みの場合、その結果を渡す
					model.addAttribute("votedItem_" + voteItem.getVoteItemId(), existingVote.getVoteValue());
				}
			}
		}**/

		// 投票アイテムのリストをビューに渡す
		model.addAttribute("voteItems", voteItems);

		//新規投票作成用フォーム
		model.addAttribute("voteItem", new VoteItem());

		return "home";
	}

	//投票の新規作成・変更更新・削除
	@PostMapping("/home")
	public String createVoteItem(
			@RequestParam("action") String action,
			VoteItem voteItem,
			@RequestParam(value = "id", required = false) Integer id,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

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
			HttpServletRequest request,
			Model model) {
		HttpSession session = request.getSession();
		//セッションからユーザー情報取得
		User user = (User) session.getAttribute("user");
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
