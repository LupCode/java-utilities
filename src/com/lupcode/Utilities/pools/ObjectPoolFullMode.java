package com.lupcode.Utilities.pools;

/**
 * Modes how an {@link ObjectPool} should behave if its 
 * capacity is reached
 * @author LupCode.com (Luca Vogels)
 * @since 2020-02-12
 */
public enum ObjectPoolFullMode {
	/** It will block until  */
	BLOCK,
	
	RETURN_NULL,
	THROW_EXCEPTION;
}
