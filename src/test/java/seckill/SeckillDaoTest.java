package seckill;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.smallchili.dao.SeckillDao;
@RunWith(SpringJUnit4ClassRunner.class)
//
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

	@Autowired
	private  SeckillDao seckill;
	
	@Test
	public  void testQueryById() {	
		System.out.println(seckill.queryById(1001));		
	}
	
	@Test
	public  void testQueryAll() {	
		System.out.println(seckill.queryAll(0,100));		
	}
	
	@Test
	public  void testReduceNumber() {	
		System.out.println(seckill.reduceNumber(1002, new Date()));
	}
	
	
}
