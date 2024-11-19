package com.example.app.domain;

import java.sql.Date;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {

	private Integer userId;
	private String userName;
	private String password;
	private String email;
	private Date birthday;
	private String memo;
	private LocalDateTime createdAt;
}
