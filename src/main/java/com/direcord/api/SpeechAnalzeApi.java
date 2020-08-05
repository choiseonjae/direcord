package com.direcord.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.direcord.model.Speaking;
import com.direcord.service.SpeechSpeakerAnalyst;

@RestController()
@RequestMapping(value = "/speech")
public class SpeechAnalzeApi {

	private static final Logger logger = LoggerFactory.getLogger(SpeechAnalzeApi.class);

	@GetMapping("/speaker/uri")
	public String speakerUri(String gscUri, int minSpeakerCnt, int maxSpeakerCnt) {
		try {
			List<Speaking> list = SpeechSpeakerAnalyst.getInstance().analyzeToUri(gscUri, minSpeakerCnt, maxSpeakerCnt);
			String str = "";
			for(Speaking speaking : list) {
				str += speaking.toString();
			}
			return str;
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
