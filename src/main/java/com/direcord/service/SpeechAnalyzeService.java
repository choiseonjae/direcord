package com.direcord.service;

import java.util.List;

import com.direcord.model.Speaking;

public interface SpeechAnalyzeService {
	
	List<Speaking> analyze(String uri, int minSpeakerCnt, int maxSpeakerCnt, String language) throws Exception;

}
