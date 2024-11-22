package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.VoteItem;

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
}
