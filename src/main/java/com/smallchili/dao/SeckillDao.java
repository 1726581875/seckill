package com.smallchili.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.smallchili.entity.Seckill;

public interface SeckillDao {

	/**
	 * 减库存
	 * @param seckillId
	 * @param killTime
	 * @return 如果影响行数>1，表示更新记录行数
	 */
	int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);
	
	/**
	 * 根据id查询秒杀对象
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(long seckillId);
	
	/**
	 * 根据偏移量查询秒杀商品列表
	 * @param offset 偏移量
	 * @param limit 多少条
	 * @return
	 */
	//java没有保存形参的记录，arg0,arg1,当传多个参数时无法对应到mybatis,要加注解@Param指定
	List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
	
}
