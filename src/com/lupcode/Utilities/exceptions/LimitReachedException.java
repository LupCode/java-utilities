package com.lupcode.Utilities.exceptions;

public class LimitReachedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_MESSAGE = "Limit has been reached";
	
	public LimitReachedException(){
		super(DEFAULT_MESSAGE);
	}
	
	public LimitReachedException(String message){
		super(message);
	}
	
	public LimitReachedException(Throwable throwable){
		super(DEFAULT_MESSAGE, throwable);
	}
	
	public LimitReachedException(String message, Throwable throwable){
		super(message, throwable);
	}
	
}
