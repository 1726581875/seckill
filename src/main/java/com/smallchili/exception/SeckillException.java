package com.smallchili.exception;

/**
 * 秒杀相关业务异常
 *继承RuntimeException (运行期异常)
 *spring的声明式事务只支持运行时异常的回滚
 */
public class SeckillException extends RuntimeException{

	public SeckillException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public SeckillException(String message) {
		super(message);
		
	}

	
}
