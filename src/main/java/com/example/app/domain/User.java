package com.example.app.domain;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class User {

	private Integer userId;
	
	@NotBlank
	@Size(min=5, max=50)
	private String userName;
	
	@NotNull
	@Size(min=5, max=60)
	private String password;
	
	@Email
	private String email;
	
	@NotNull
	@Past
	private Date birthday;
	private String memo;
	private LocalDateTime createdAt;
}
