package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.VoteResult;

@Mapper
public interface VoteResultMapper {

	List<VoteResult> selectAll();
	
	
	//個別取得
	VoteResult getVoteResultById(int id);
	
	//追加
	void addVoteResult(VoteResult voteResult);
	
	//更新
	void updateVoteResult(VoteResult voteResult);
	
	//削除
	void deleteVoteResult(int id);
}
