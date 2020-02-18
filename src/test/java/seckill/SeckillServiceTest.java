package seckill;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.smallchili.dto.Exposer;
import com.smallchili.dto.SeckillExecution;
import com.smallchili.entity.Seckill;
import com.smallchili.exception.RepeatKillException;
import com.smallchili.exception.SeckillCloseException;
import com.smallchili.service.SeckillService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
	                   "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
	private SeckillService seckillService;
	
	
	@Test
	public void testGetSeckillList(){
		List<Seckill> seckillList = seckillService.getSeckillList();
		logger.info("list={}",seckillList);
	}
	
	@Test
	public void testGetById(){
		Seckill seckill = seckillService.getById(1000);
		logger.info("seckill={}",seckill);
	}
	
	@Test
	public void testExportSeckillUrl(){
		long id = 1002;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		logger.info("exposer={}",exposer);
/*		 查出    exposed=true, 
				md5=6a999604e3bd9228a13ebf10ddb23909,
				seckillId=1002*/
	}
	
	@Test
	public void testExecuteSeckill(){
		long id = 1002;
		long phone = 13922077691L;
		String md5 = "6a999604e3bd9228a13ebf10ddb23909";
		try{
		SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
		logger.info("seckillExecution={}",seckillExecution);
		}catch(SeckillCloseException e){
			 logger.error(e.getMessage());  
		  }catch(RepeatKillException e){
			 logger.error(e.getMessage());   
		  }
	}
	
  //结合上面两个测试，集成测试代码完整逻辑，注意可重复执行
	@Test
	public void testSeckill(){
		long id = 1001;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		logger.info("exposer={}",exposer);
	if(exposer.isExposed()){
		long phone = 13922077691L;
		String md5 = exposer.getMd5();
		try{
		SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
		logger.info("seckillExecution={}",seckillExecution);
		}catch(SeckillCloseException e){
			 logger.error(e.getMessage());  
		  }catch(RepeatKillException e){
			 logger.error(e.getMessage());   
		  }
	  }else{//秒杀未开启
			logger.warn("exposer={}",exposer);
		} 
	}
	
	
}
