package com.direcord.service;

public interface SpeechAnalyst {
	
	public String analyze(String fileName, int minSpeakerCnt, int maxSpeakerCnt) throws Exception;

}
