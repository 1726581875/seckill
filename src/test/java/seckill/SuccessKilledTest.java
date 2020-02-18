package seckill;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.smallchili.dao.SuccessKilledDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledTest {

	@Autowired
	SuccessKilledDao successKilled;
	
	@Test
	public void testInsertSuccessKilled(){
		long id = 1000L;
		long phone = 12345678910L;
		successKilled.insertSuccessKilled(id, phone);
	}	
	
	@Test
	public void testQueryByIdWithSeckill(){
		long id = 1000L;
		long phone = 12345678910L;
		
		System.out.println(successKilled.queryByIdWithSeckill(id, phone));
	}
	
}
