package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.app.domain.VoteItem;
import com.example.app.mapper.VoteItemMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor
public class VoteItemController {

	private final VoteItemMapper voteItemmapper;
	
	//Homeページへ
	@GetMapping("/home")
	public String homePage(
			Model model,
			HttpServletRequest request
			) {
		HttpSession session = request.getSession();
	  String userName = (String) session.getAttribute("user");
	  
	  //セッションにユーザー情報があれば、ユーザー情報をモデルに追加・表示
	  if(userName != null) {
	  	model.addAttribute("userName", userName);
	  }
	  
	  
	  //ほかのユーザーの投票の投稿見る、投票する、投票結果見る
	  //全ての投票を取得・
	  model.addAttribute("voteItems", voteItemmapper.selectAll());
	  
	  //新規投票作成用フォーム
		model.addAttribute("voteItem", new VoteItem());
		return "home";
	}
	
	
	//投票の新規作成・変更更新・削除
	@PostMapping("/home")
	public String createVoteItem(
			VoteItem voteItem
			) {
		//新規作成
		voteItemmapper.addVoteItem(voteItem);
		return "redirect:/home";
	}

	@PostMapping("/home")
	public String deleteVoteItem(Integer id) {
		//削除
		voteItemmapper.deleteVoteItem(id);
		return "redirect:/home";
	}
	
	@PostMapping("/home")
	public String updateVoteItem(VoteItem voteItem) {
		//更新
		voteItemmapper.updateVoteItem(voteItem);
		return "redirect:/home";
	}
	
	
}