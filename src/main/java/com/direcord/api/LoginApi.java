package com.direcord.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.direcord.service.LoginService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

@RestController()
@RequestMapping(value = "/login")
public class LoginApi {

	@Autowired
	private LoginService loginService;

	@GetMapping
	// TODO returnCode, returnMsg 생성 부분 AOP 로 뺴기
	public boolean logout(String idToken) {
		try {
			loginService.cancelToken(idToken);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@GetMapping("/check")
	public boolean isLogin(String idToken) {
		try {
			FirebaseApp.initializeApp();
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
			String uid = decodedToken.getUid();
			System.out.println("[uid] " + uid);
			return true;
		} catch (Throwable e) {
			System.out.println("도달 완료 error");
			e.printStackTrace();
			return false;
		}
	}

	@GetMapping("/{idToken}")
	public boolean login(@PathVariable("idToken") String idToken) {
		try {
			FirebaseApp.initializeApp();
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
			String uid = decodedToken.getUid();
			System.out.println("[uid] " + uid);
			return true;
		} catch (Throwable e) {
			System.out.println("도달 완료 error");
			e.printStackTrace();
			return false;
		}
	}

	@GetMapping("/")
	public boolean index() {
		return true;
	}
}
