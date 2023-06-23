package com.house.item.domain;

import java.io.Serializable;

import com.house.item.entity.User;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SessionUser implements Serializable {
	private Long userNo;
	private String username;

	public User toUser() {
		return User.builder()
			.userNo(userNo)
			.build();
	}
}
