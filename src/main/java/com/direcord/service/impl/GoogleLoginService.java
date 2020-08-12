package com.direcord.service.impl;

import org.springframework.stereotype.Service;

import com.direcord.exception.ReasonException;
import com.direcord.service.LoginService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

@Service
public class GoogleLoginService implements LoginService {

	@Override
	public void checkCancelToken(String idToken) throws Exception {
		getUidFormToken(idToken);
	}

	@Override
	public void cancelToken(String idToken) throws Exception {
		String uid = getUidFormToken(idToken);
		FirebaseAuth.getInstance().revokeRefreshTokens(uid);
		UserRecord user = FirebaseAuth.getInstance().getUser(uid);
		// Convert to seconds as the auth_time in the token claims is in seconds too.
		long revocationSecond = user.getTokensValidAfterTimestamp() / 1000;
		System.out.println("Tokens revoked at: " + revocationSecond);
	}

	@Override
	public String getUidFormToken(String idToken) throws Exception {
		try {
			// Verify the ID token while checking
			// if the token is revoked by passing checkRevoked as true.
			boolean checkRevoked = true;
			FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken, checkRevoked);
			return decodedToken.getUid(); // Token is valid and not revoked.
		} catch (FirebaseAuthException e) {
			if (e.getErrorCode().equals("id-token-revoked")) {
				// Token has been revoked.
				// Inform the user to re-authenticate or signOut() the user.
				throw new ReasonException("TOKEN_REVOKED", e.getMessage());
			} else {
				throw new ReasonException("TOKEN_INVALID", e.getMessage());
			}
		}
	}

}
