package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.app.domain.VoteItem;
import com.example.app.domain.VoteResult;

@Mapper
public interface VoteItemMapper {
  //全取得
	List<VoteItem> selectAll();
	
	//個別取得
	VoteItem getVoteItemById(int id);
	
	//追加
	void addVoteItem(VoteItem voteItem);
	
	//更新
	void updateVoteItem(VoteItem voteItem);
	
	//削除
	void deleteVoteItem(int id);
	
	//追加：vote_resultsのvote_result_idが必要
	Integer addVoteResult(VoteResult voteResult);
	
	void updateVoteCount(Integer voteItemId);
	
  // 追加：ユーザーが特定の投票アイテムに投票した結果を取得する
  VoteResult findVoteResultByUserIdAndVoteItemId(@Param("voteItemId") Integer voteItemId, @Param("userId") Integer userId);
}
