package com.direcord.exception;

import lombok.Getter;

@Getter
public class ReasonException extends Exception {

	private static final long serialVersionUID = 5369166562162105143L;
	private String reasonKey;
	private String errMsg;

	public ReasonException(String reasonKey, String errMsg) {
		this.reasonKey = reasonKey;
		this.errMsg = errMsg;
	}

}
