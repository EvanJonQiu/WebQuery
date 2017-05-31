package com.esrisy.WebQuery.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.esrisy.WebQuery.common.MonthGroupResult;
import com.esrisy.WebQuery.common.NewSourceGroupResult;
import com.esrisy.WebQuery.common.QueryResult;
import com.esrisy.WebQuery.common.ResultList;
import com.esrisy.WebQuery.services.IQueryService;

@Controller
@RequestMapping(value="query")
public class QueryController {
	
	private static final Logger logger = LoggerFactory.getLogger(QueryController.class);
	
	@Autowired
	private IQueryService queryService;

	@RequestMapping(value="query.do")
	@ResponseBody
	public ResultList<QueryResult> query(String key) {
		logger.debug("QueryController.query");
		
		ResultList<QueryResult> list = queryService.query(key);
		return list;
	}
	
	@RequestMapping(value="queryMonthGroup")
	@ResponseBody
	public ResultList<MonthGroupResult> queryMonthGroup(String key) {
		logger.debug("QueryController.queryMonthGroup");
		
		ResultList<MonthGroupResult> list = queryService.queryMonthGroup(key);
		return list;
	}
	
	@RequestMapping(value="queryNewSourceGroup")
	@ResponseBody
	public ResultList<NewSourceGroupResult> queryNewSourceGroup(String key) {
		logger.debug("QueryController.queryNewSourceGroup");
		
		ResultList<NewSourceGroupResult> list = queryService.queryNewSourceGroup(key);
		return list;
	}
	
	@RequestMapping(value="booleanQuery")
	@ResponseBody
	public ResultList<QueryResult> booleanQuery(String key) {
	    logger.debug("QueryController.booleanQuery");
	    
	    ResultList<QueryResult> list = queryService.booleanQuery(key);
        return list;
	}
}
