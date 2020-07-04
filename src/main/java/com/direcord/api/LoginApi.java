package com.direcord.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/")
@RestController()
public class LoginApi {
	
//	@PostMapping(path = "/", consumes = "multipart/form-data")
//		public long index(MultipartFile files) {
//		return files.getSize();//	}
	
	@GetMapping()
	public String index() {
		return "성공입니다.";
	}
	
	@PostMapping()
	public boolean signIn() {
		return false;
	}

}
