package com.example.app.domain;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import com.example.app.validation.AddUserGroup;
import com.example.app.validation.LoginGroup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class User {

	private Integer userId;
	//未入力＝会員登録時・ログイン時
	@NotBlank(groups = {LoginGroup.class, AddUserGroup.class})
	//文字数＝会員登録時
	@Size(min=5, max=50, groups = {AddUserGroup.class})
	private String userName;
	
	//未入力＝会員登録時・ログイン時
	@NotBlank(groups = {LoginGroup.class, AddUserGroup.class})
	@Size(min=5, max=60, groups = {AddUserGroup.class})
	private String password;
	
	@Email
	private String email;
	
	@NotNull(groups = {AddUserGroup.class})
	@Past(groups = {AddUserGroup.class})
	//@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
	private String memo;
	private LocalDateTime createdAt;

	//ユーザーに対しユーザー投稿 1対多　多対多
	public List<VoteItem> votes;
	
	//ユーザー、ユーザー投稿に対し、投稿結果　1対多　多対多
	public List<VoteResult> results;
}
