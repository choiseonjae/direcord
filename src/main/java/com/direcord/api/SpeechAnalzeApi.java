package com.direcord.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.direcord.service.QuickstartSample;
import com.direcord.service.SpeechSpeakerAnalyst;

@RequestMapping(value = "/speech")
@RestController()
public class SpeechAnalzeApi {
	
	 private static final Logger logger = LoggerFactory.getLogger(SpeechAnalzeApi.class);
	 
	@GetMapping("/wav")
	public String wav(String fileName) {
		try {
			return QuickstartSample.callSTTOfWav(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL : " + e.getClass().getSimpleName() + " MSG : "+ e.getMessage();
		}
	}
	
	@GetMapping("/flac")
	public String flac(String fileName) {
		try {
			return QuickstartSample.callSTTOfFlac(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL : " + e.getClass().getSimpleName() + " MSG : "+ e.getMessage();
		}
	}
	
	@GetMapping("/speaker")
	public String speaker(String fileName, int minSpeakerCnt, int maxSpeakerCnt) {
		try {
			return SpeechSpeakerAnalyst.getInstance().analyze(fileName, minSpeakerCnt, maxSpeakerCnt);
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL : " + e.getClass().getSimpleName() + " MSG : "+ e.getMessage();
		}
	}
	
	@GetMapping("/speaker/uri")
	public String speakerUri(String gscUri, int minSpeakerCnt, int maxSpeakerCnt) {
		try {
			return SpeechSpeakerAnalyst.getInstance().analyzeToUri(gscUri, minSpeakerCnt, maxSpeakerCnt);
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL : " + e.getClass().getSimpleName() + " MSG : "+ e.getMessage();
		}
	}

}
