package com.direcord.api;

import java.io.FileInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.direcord.service.LoginService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

@RestController()
@RequestMapping(value = "/login")
public class LoginApi {

	@Autowired
	private LoginService loginService;

	@GetMapping("/logout/{idToken}")
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

			FirebaseApp firebaseApp = null;
			List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
			 
			if(firebaseApps != null && !firebaseApps.isEmpty()){
			             
			    for(FirebaseApp app : firebaseApps){
			        if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
			            firebaseApp = app;
			        }
			    }
			             
			}else{
				FileInputStream serviceAccount = new FileInputStream("/home/creativenotist/google-services.json");
			    FirebaseOptions options = new FirebaseOptions.Builder()
			        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
			        .setDatabaseUrl("https://direcord-283711.firebaseio.com")
			        .setProjectId("direcord-283711")
			        .build();
			    firebaseApp = FirebaseApp.initializeApp(options);              
			}


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
