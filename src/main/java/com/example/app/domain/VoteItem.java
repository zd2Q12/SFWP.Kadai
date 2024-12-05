package com.example.app.domain;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VoteItem {

	private Integer voteItemId;

	@NotBlank
	@Size(max = 255)
	private String title;

	private String description;
	private Integer createdBy;//user.javaのidもしくはログイン情報から取得

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime votingStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime votingEnd;

	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;
	
  private Integer agreeCount = 0;  // 賛成票の数
  private Integer disagreeCount = 0;  // 反対票の数

}
