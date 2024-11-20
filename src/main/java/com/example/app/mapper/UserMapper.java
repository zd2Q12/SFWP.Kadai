package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.User;

@Mapper
public interface UserMapper {
//CRUD処理
	//管理者用
	List<User> selectUsers();
	
	//個別取得　（Read）
	User getUserById(int id);//IDに対応するUserを取得・表示する
	
//追加（Create）
	void addUser(User user);//戻り値がないので、新規ユーザーをDBに格納するのみ
	
	//更新(Update)
	void updateUser(User user);
	
	//削除(Delete)
	void deleteUser(int id);
}
