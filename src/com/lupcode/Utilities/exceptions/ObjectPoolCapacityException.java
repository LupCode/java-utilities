package com.lupcode.Utilities.exceptions;

public class ObjectPoolCapacityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_MESSAGE = "Capacity of pool is exhausted";
	
	public ObjectPoolCapacityException(){
		super(DEFAULT_MESSAGE);
	}
	
	public ObjectPoolCapacityException(String message){
		super(message);
	}
	
	public ObjectPoolCapacityException(Throwable throwable){
		super(DEFAULT_MESSAGE, throwable);
	}
	
	public ObjectPoolCapacityException(String message, Throwable throwable){
		super(message, throwable);
	}
	
}
