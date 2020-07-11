package com.direcord.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.direcord.service.QuickstartSample;

@RequestMapping(value = "/login")
@RestController()
public class LoginApi {
	
	 private static final Logger logger = LoggerFactory.getLogger(LoginApi.class);
	 
//	@PostMapping(path = "/", consumes = "multipart/form-data")
//		public long index(MultipartFile files) {
//		return files.getSize();//	}
	
	@GetMapping("/signIn")
	public boolean index(String id, String pwd) {
		if(id.equalsIgnoreCase("admin") && pwd.equalsIgnoreCase("admin")) {
			return true;
		}
		return false;
	}
	
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
	public String speaker(String fileName) {
		try {
			return QuickstartSample.callDistinguishSpeaker(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL : " + e.getClass().getSimpleName() + " MSG : "+ e.getMessage();
		}
	}

}
