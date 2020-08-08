package com.direcord.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

@RestController()
@RequestMapping(value = "/login")
public class LoginApi {
	@GetMapping("/check/login")
	public boolean isLogin(String idToken) {
		try {
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
			String uid = decodedToken.getUid();
			System.out.println("[uid] " + uid);
			return true;
		} catch (FirebaseAuthException e) {
			e.printStackTrace();
			return false;
		}
	}

	@GetMapping("/")
	public boolean index() {
		return true;
	}
}
