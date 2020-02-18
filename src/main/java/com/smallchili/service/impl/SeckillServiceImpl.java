package com.smallchili.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.smallchili.dao.SeckillDao;
import com.smallchili.dao.SuccessKilledDao;
import com.smallchili.dto.Exposer;
import com.smallchili.dto.SeckillExecution;
import com.smallchili.entity.Seckill;
import com.smallchili.entity.SuccessKilled;
import com.smallchili.enums.SeckillStateEnum;
import com.smallchili.exception.RepeatKillException;
import com.smallchili.exception.SeckillCloseException;
import com.smallchili.exception.SeckillException;
import com.smallchili.service.SeckillService;

@Service
public class SeckillServiceImpl implements SeckillService{

    public Logger logger = LoggerFactory.getLogger(this.getClass());
	
    //注入Service依赖
    @Autowired
	private  SeckillDao seckillDao;
     
    @Autowired
	private SuccessKilledDao successKilledDao;
	//盐值,用于混淆md5
	private final String slat = "s`hfw+7y984.wt78gut7*^874r&%&7s^usgi~!@#$G";
	
	@Override
	public List<Seckill> getSeckillList() {
		
		return seckillDao.queryAll(0, 4);
	}

	@Override
	public Seckill getById(long seckillId) {
	
		return seckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		//先查询秒杀商品
		Seckill seckill = seckillDao.queryById(seckillId);
		if(seckill == null){//如果为空,返回false
			return new Exposer(false, seckillId);
		}
		//如果不为空,获取商品的秒杀开始时间和秒杀结束时间
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		//获取系统当前时间
		Date nowTime = new Date();
		//判断是否是秒杀时间段,如果不是秒杀时间,返回秒杀时间和当前时间
		if(nowTime.getTime() < startTime.getTime()
				|| nowTime.getTime() > endTime.getTime()){
			return new Exposer(false, seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
		}
		
		//转化特定字符串,不可逆
		String md5 = getMD5(seckillId);
		
		return new Exposer(true, md5, seckillId);
	}
	
	private String getMD5(long seckillId){
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}
	

	@Override
	@Transactional
	/**
	 * 使用注解控制事务方法的优点
	 * 1.开发团队达成一致约定,明确标注事务方法的编程风格
	 * 2.保证事务方法的执行时间尽可能短,不要穿插网络操作RPC/HTTP请求或者剥离到事务方法外部,
	 * 3.不是所有的方法都需要事务 
	 * */
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			  throw new SeckillException("seckill data rewrite");
		}
		//执行秒杀逻辑:减库存 + 记录购买行为
		Date nowTime = new Date();
  try{
		//减库存
		int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
		if(updateCount <= 0){
			//没有更新到记录,秒杀结束
			throw new SeckillCloseException("seckill is close");
		}else{
			//记录购买行为
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			//唯一联合主键:seckillId,userPhone
			 if(insertCount <= 0){
				//重复秒杀
				throw new RepeatKillException("seckill repeated");
			 }else{
				//秒杀成功
		    	 SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				 return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
			  }
			
		}
			
  }catch(SeckillCloseException e1){
	  throw e1;	  
  }catch(RepeatKillException e2){
	  throw e2;	  
  }catch(Exception e){
	     logger.error(e.getMessage());
	     //把所有的编译期异常 转化为运行期异常
	     throw new SeckillException("seckill inner error:"+e.getMessage());
  }   
		

  
	}

}
