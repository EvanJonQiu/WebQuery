package com.esrisy.WebQuery.services;

import com.esrisy.WebQuery.common.MonthGroupResult;
import com.esrisy.WebQuery.common.NewSourceGroupResult;
import com.esrisy.WebQuery.common.QueryResult;
import com.esrisy.WebQuery.common.ResultList;

public interface IQueryService {

	public ResultList<QueryResult> query(String key);
	
	public ResultList<MonthGroupResult> queryMonthGroup(String key);
	
	public ResultList<NewSourceGroupResult> queryNewSourceGroup(String key);
	
	public ResultList<QueryResult> booleanQuery(String key);
}
