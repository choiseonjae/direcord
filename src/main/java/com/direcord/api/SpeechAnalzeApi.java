package com.direcord.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.direcord.model.Speaking;
import com.direcord.service.LoginService;
import com.direcord.service.SpeechAnalyzeService;

@RestController()
@RequestMapping(value = "/speech")
public class SpeechAnalzeApi {
	
	@Autowired
	private SpeechAnalyzeService speechAnalyzeService;
	
	@Autowired
	private LoginService loginService;

	@GetMapping(path = "/analysis/{idToken}/{uri}")
	public List<Speaking> analyze(@PathVariable("idToken") String idToken, @PathVariable("uri") String uri, int minSpeakerCnt, int maxSpeakerCnt) {
		try {
			// TODO 현재는 해당 위치에서 token 유효성 검사를 진행. 추후에 AOP로 뺄 것
			loginService.checkCancelToken(idToken);
			
			return speechAnalyzeService.analyze(uri, minSpeakerCnt, maxSpeakerCnt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
