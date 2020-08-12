package com.direcord.service;

public interface LoginService {
	
	public void checkCancelToken(String idToken) throws Exception;
	
	public String getUidFormToken(String idToken) throws Exception;
	
	public void cancelToken(String idToken) throws Exception;
	
}
