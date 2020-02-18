package com.smallchili.exception;

/**
 * 秒杀关闭异常
 * 秒杀关闭后不允许再秒杀
 *
 */
public class SeckillCloseException extends SeckillException{

	public SeckillCloseException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public SeckillCloseException(String message) {
		super(message);
		
	}

}
