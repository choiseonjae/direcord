package com.direcord.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
	@PostMapping()
	public boolean signIn() {
		return false;
	}

}
