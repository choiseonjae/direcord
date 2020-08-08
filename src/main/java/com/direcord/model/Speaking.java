package com.direcord.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Speaking {

	private int index;
	private StringBuilder speaking = new StringBuilder();
	private String startTime;
	private String endTime;

	@Override
	public String toString() {
		String str = "Speaker ";
		str += index + " : ";
		str += speaking + "(" + startTime + "~" + endTime + ")";
		return str;
	}

	public Speaking(int index) {
		super();
		this.index = index;
	}

	public void recordTalking(String word) {
		speaking.append(word);
		speaking.append(" ");
	}
}
