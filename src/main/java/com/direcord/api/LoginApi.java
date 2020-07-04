package com.direcord.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.direcord.service.QuickstartSample;

@RequestMapping(value = "/login")
@RestController()
public class LoginApi {
	
	
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
	
	@GetMapping("/")
	public String index() {
		try {
			QuickstartSample.callSTT();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Test URL";
	}

}
