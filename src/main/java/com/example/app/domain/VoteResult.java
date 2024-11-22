package com.example.app.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class VoteResult {

	private Integer voteResultId;
	private Integer userId;
	private Integer voteItemId;
	private LocalDateTime votedAt;
}
