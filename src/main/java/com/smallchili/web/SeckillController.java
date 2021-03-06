package com.smallchili.web;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smallchili.dto.Exposer;
import com.smallchili.dto.SeckillExecution;
import com.smallchili.dto.SeckillResult;
import com.smallchili.entity.Seckill;
import com.smallchili.enums.SeckillStateEnum;
import com.smallchili.exception.RepeatKillException;
import com.smallchili.exception.SeckillCloseException;
import com.smallchili.exception.SeckillException;
import com.smallchili.service.SeckillService;

@Controller
@RequestMapping("/seckill")
public class SeckillController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	
	
	@RequestMapping(value = "/list" ,method = RequestMethod.GET)
	public String list(Model model){
		List<Seckill> seckillList = seckillService.getSeckillList();
		model.addAttribute("list",seckillList);
		return "list";
	}
	
	@RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId ,Model model){
		if(seckillId == null){
			return "redirect:/seckill/list";
		}	
		Seckill seckill = seckillService.getById(seckillId);
		if(seckill == null){
			return "forward:/seckill/list";
		}
		
		model.addAttribute("seckill", seckill);		
		return "detail";		
	}
	
	//ajax  json
	
	@RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST,
			       produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
		
		SeckillResult<Exposer> result;
		try{
	    Exposer exposer = seckillService.exportSeckillUrl(seckillId);
	    result = new SeckillResult<Exposer>(true, exposer);
		}catch(Exception e){
			logger.error(e.getMessage());
			result = new SeckillResult<Exposer>(false, e.getMessage());
		}
		 return result;
	}

	@RequestMapping(value = "/{seckillId}/{md5}/execution",
			        method = RequestMethod.POST,
			       produces = {"application/json;charset=UTF-8"})
	@ResponseBody
public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
			                                       @PathVariable("md5") String md5,
			                                       @CookieValue(value = "killPhone",required = false) Long phone){
    if(phone == null){
    	   return new SeckillResult<SeckillExecution>(false,"用户未登陆");
    }
	SeckillResult<SeckillExecution> result;
		try{
		SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
		return new SeckillResult<SeckillExecution>(true,seckillExecution);
		}catch(SeckillCloseException e1){
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.END);
			return new SeckillResult<SeckillExecution>(false,execution);
		  }catch(RepeatKillException e2){
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExecution>(false,execution);
		  }catch(Exception e){
			logger.error(e.getMessage());
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(false,execution);
		}
		
	}
	
	@RequestMapping(value = "/time/now" ,method = RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time(){
		Date now = new Date();
		return new SeckillResult<Long>(true,now.getTime());
	}
	
}
