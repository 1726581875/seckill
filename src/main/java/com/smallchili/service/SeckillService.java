package com.smallchili.service;

import java.util.List;

import com.smallchili.dto.Exposer;
import com.smallchili.dto.SeckillExecution;
import com.smallchili.entity.Seckill;
import com.smallchili.exception.RepeatKillException;
import com.smallchili.exception.SeckillCloseException;
import com.smallchili.exception.SeckillException;

/**
 * 秒杀相关业务接口
 *
 */
public interface SeckillService {
	
	/**查询所有秒杀记录
	 * @return
	 */
	List<Seckill> getSeckillList();
	
    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);
	
    /**
     * 秒杀开启时是输出秒杀接口地址,
     * 否则输出系统时间和秒杀时间
     * @param seckillId
     */
     Exposer exportSeckillUrl(long seckillId);
    
     
     /**
      * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
      throws SeckillException,RepeatKillException,SeckillCloseException;
     
     
}
