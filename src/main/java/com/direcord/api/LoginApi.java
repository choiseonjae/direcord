package com.direcord.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginApi {
	
//	@PostMapping(path = "/", consumes = "multipart/form-data")
//	public long index(MultipartFile files) {
//		return files.getSize();//	}
	
	@GetMapping(path = "/")
	public String index() {
		return "성공입니다s.";
	}
	
	@GetMapping(path = "/singIn")
	public boolean signIn() {
		
		
		
		return false;
	}

}
