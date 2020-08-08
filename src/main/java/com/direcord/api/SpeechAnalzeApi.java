package com.direcord.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.direcord.model.Speaking;
import com.direcord.service.SpeechSpeakerAnalyst;
import com.direcord.service.Uploader;

@RestController()
@RequestMapping(value = "/speech")
public class SpeechAnalzeApi {

	private static final Logger logger = LoggerFactory.getLogger(SpeechAnalzeApi.class);

	@GetMapping("/speaker/upload")
	public String upload(String objectName, String filePath) {
//	public String speakerUri(String gscUri, int minSpeakerCnt, int maxSpeakerCnt) {
		try {
			Uploader.uploadObject(objectName, filePath);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "얍";
	}

	@GetMapping("/speaker/uri")
	public List<Speaking> speakerUri(String gscUri, int minSpeakerCnt, int maxSpeakerCnt) {
//	public String speakerUri(String gscUri, int minSpeakerCnt, int maxSpeakerCnt) {
		try {
			List<Speaking> list = SpeechSpeakerAnalyst.getInstance().analyzeToUri(gscUri, minSpeakerCnt, maxSpeakerCnt);
			return list;
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
