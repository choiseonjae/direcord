package com.direcord.model;

import com.google.protobuf.Duration;

public class Speaking {

	private int index;
	private StringBuilder speaking = new StringBuilder();
	private Duration startTime;
	private Duration endTime;
	
	@Override
	public String toString() {
		String str = "Speaker ";
		str += index + " : ";
		str += speaking + "(" + startTime + "~" + endTime +")";
		return str;
	}


	public Speaking(int index) {
		super();
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public String getSpeaking() {
		return speaking.toString();
	}

	
	public void recordTalking(String word) {
		speaking.append(word);
		speaking.append(" ");
	}

	public Duration getStartTime() {
		return startTime;
	}

	public void setStartTime(Duration duration) {
		this.startTime = duration;
	}

	public Duration getEndTime() {
		return endTime;
	}

	public void setEndTime(Duration endTime) {
		this.endTime = endTime;
	}

}
