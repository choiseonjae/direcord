package com.direcord.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.direcord.model.Speaking;
import com.direcord.service.SpeechSpeakerAnalyst;

@RestController()
@RequestMapping(value = "/speech")
public class SpeechAnalzeApi {

	@GetMapping("/speaker/upload")
	public String upload() {
		return "얍";
	}

	@PostMapping("/speaker/upload")
	public String upload(@RequestParam("file") MultipartFile file) {
		System.out.println(file.getName() + " : " + file.getSize());
//			System.out.println(objectName);
//				Uploader.uploadObject(objectName, filePath);
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
