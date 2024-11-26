package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.User;
import com.example.app.domain.VoteItem;
import com.example.app.domain.VoteResult;

@Mapper
public interface UserMapper {
//CRUD処理
	//管理者用
	List<User> selectAll();

	//個別取得　（Read）
	User getUserById(int id);//IDに対応するUserを取得・表示する
	User getUserByUserNameAndPassword(String userName, String password);
//追加（Create）
	void addUser(User user);//戻り値がないので、新規ユーザーをDBに格納するのみ
	
	//更新(Update)
	void updateUser(User user);
	
	//削除(Delete)
	void deleteUser(int id);
	
	//追加：vote_itemsのvote_item_idが必要
	Integer addVoteItem(VoteItem voteItem);
	
	//追加：vote_resultsのvote_result_idが必要
	Integer addVoteResult(VoteResult voteResult);
}
