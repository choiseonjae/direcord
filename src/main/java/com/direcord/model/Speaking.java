package com.direcord.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Speaking extends DefaultResponse{

	private int index;
	private StringBuilder speaking = new StringBuilder();
	private String startTime;
	private String endTime;

	public Speaking(int index) {
		this.index = index;
	}

	public void recordTalking(String word) {
		speaking.append(word);
		speaking.append(" ");
	}
}
