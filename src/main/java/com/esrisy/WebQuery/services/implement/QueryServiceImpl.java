package com.esrisy.WebQuery.services.implement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.esrisy.WebQuery.common.MonthGroupResult;
import com.esrisy.WebQuery.common.NewSourceGroupResult;
import com.esrisy.WebQuery.common.QueryResult;
import com.esrisy.WebQuery.common.ResultList;
import com.esrisy.WebQuery.luceneEngine.LuceneEngine;
import com.esrisy.WebQuery.services.IQueryService;

@Service
public class QueryServiceImpl implements IQueryService {
	
    private static final Logger logger = LoggerFactory.getLogger(QueryServiceImpl.class);

    public ResultList<QueryResult> query(String key) {
        logger.debug("QueryServiceImpl.query");
		
        LuceneEngine engine = LuceneEngine.getInstance();
        IndexSearcher searcher = new IndexSearcher(engine.getReader());
		
        Term term = new Term("newsContent", key);
    	Query query = new TermQuery(term);
    	
    	TopDocs results;
        try {
            results = searcher.search(query, 1000);
    	
            ScoreDoc[] hits = results.scoreDocs;
            int numTotalHits = results.totalHits;
	    	
            ResultList<QueryResult> resultList = new ResultList<QueryResult>();
            resultList.setCount(numTotalHits);
	    	
            List<QueryResult> list = new ArrayList<QueryResult>();
	    	
            for (int i = 0; i < numTotalHits; i++) {
                Document doc = searcher.doc(hits[i].doc);
	    		
                QueryResult result = new QueryResult();
                result.setId(doc.get("id"));
                result.setTitle(doc.get("title"));
                result.setAuthor(doc.get("author"));
                result.setCountry(doc.get("country"));
                result.setNewsSource(doc.get("newSource"));
                result.setPublishDate(doc.get("publishDate"));
                result.setqPublishdate(doc.get("qPublishdate"));
                result.setqPublishDataMonth(doc.get("qPublishDataMonth"));
                result.setNewsContent(doc.get("newsContent"));
                list.add(result);
            }
            resultList.setData(list);
	
            return resultList;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
	
    public ResultList<MonthGroupResult> queryMonthGroup(String key) {
        logger.debug("QueryServiceImpl.queryMonthGroup");
		
        LuceneEngine engine = LuceneEngine.getInstance();
        IndexSearcher searcher = new IndexSearcher(engine.getReader());
		
        GroupingSearch groupingSearch = new GroupingSearch("qPublishDataMonth");
        SortField sortField = new SortField("qPublishDataMonth", SortField.Type.STRING_VAL);
        groupingSearch.setGroupSort(new Sort(sortField));
        groupingSearch.setFillSortFields(true);
        groupingSearch.setCachingInMB(4.0, true);
        groupingSearch.setAllGroups(true);
        groupingSearch.setGroupDocsLimit(10);
    	
    	ResultList<MonthGroupResult> resultList = new ResultList<MonthGroupResult>();
    	
    	List<MonthGroupResult> list = new ArrayList<MonthGroupResult>();
    	int totalCount = 0;
        try {
            IKAnalyzer analyzer = new IKAnalyzer(true);
            TokenStream tokenStream = analyzer.tokenStream("content", key);
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            
            BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
            
            while (tokenStream.incrementToken()) {
                Term term = new Term("newsContent", charTermAttribute.toString());
                Query query = new TermQuery(term);
                booleanQueryBuilder.add(query, BooleanClause.Occur.SHOULD);
            }
            tokenStream.close();
            
            TermRangeQuery termRangeQuery = TermRangeQuery.newStringRange("qPublishdate", "20160101", "20160331", true, true);
            booleanQueryBuilder.add(termRangeQuery, BooleanClause.Occur.MUST);
            
            TopGroups<BytesRef> result = groupingSearch.search(searcher, booleanQueryBuilder.build(), 0, 1000);
			
            GroupDocs<BytesRef>[] docs = result.groups;
            for (GroupDocs<BytesRef> groupDocs : docs) {
                logger.debug("group:" + new String(groupDocs.groupValue.bytes));
                logger.debug(new Integer(groupDocs.totalHits).toString());
                totalCount += groupDocs.totalHits;
		    	
                MonthGroupResult temp = new MonthGroupResult();
                temp.setCount(groupDocs.totalHits);
                temp.setGroupMonth(new String(groupDocs.groupValue.bytes));
                list.add(temp);
            }
            resultList.setCount(totalCount);
            resultList.setData(list);
		    
            return resultList;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
	
    public ResultList<NewSourceGroupResult> queryNewSourceGroup(String key) {
        logger.debug("QueryServiceImpl.queryNewSourceGroup");
		
        LuceneEngine engine = LuceneEngine.getInstance();
        IndexSearcher searcher = new IndexSearcher(engine.getReader());
		
        GroupingSearch groupingSearch = new GroupingSearch("qNewsSource");
        groupingSearch.setGroupSort(new Sort(SortField.FIELD_SCORE));
        groupingSearch.setFillSortFields(true);
        groupingSearch.setCachingInMB(4.0, true);
        groupingSearch.setAllGroups(true);
        groupingSearch.setGroupDocsLimit(10);
    	
    	ResultList<NewSourceGroupResult> resultList = new ResultList<NewSourceGroupResult>();
    	
    	List<NewSourceGroupResult> list = new ArrayList<NewSourceGroupResult>();
    	int totalCount = 0;
        try {
            IKAnalyzer analyzer = new IKAnalyzer(true);
            TokenStream tokenStream = analyzer.tokenStream("content", key);
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            
            BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
            
            while (tokenStream.incrementToken()) {
                Term term = new Term("newsContent", charTermAttribute.toString());
                Query query = new TermQuery(term);
                booleanQueryBuilder.add(query, BooleanClause.Occur.SHOULD);
            }
            tokenStream.close();
            
            TermRangeQuery termRangeQuery = TermRangeQuery.newStringRange("qPublishdate", "20160101", "20161231", true, true);
            booleanQueryBuilder.add(termRangeQuery, BooleanClause.Occur.MUST);
            
            TopGroups<BytesRef> result = groupingSearch.search(searcher, booleanQueryBuilder.build(), 0, 1000);
			
            GroupDocs<BytesRef>[] docs = result.groups;
            for (GroupDocs<BytesRef> groupDocs : docs) {
                logger.debug("group:" + new String(groupDocs.groupValue.bytes, "utf8"));
                logger.debug(new Integer(groupDocs.totalHits).toString());
                totalCount += groupDocs.totalHits;
		    	
                NewSourceGroupResult temp = new NewSourceGroupResult();
                temp.setCount(groupDocs.totalHits);
                temp.setNewSource(new String(groupDocs.groupValue.bytes, "utf8"));
                list.add(temp);
            }
            resultList.setCount(totalCount);
            resultList.setData(list);
		    
            return resultList;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
	
    public ResultList<QueryResult> booleanQuery(String key) {
        logger.debug("QueryServiceImpl.booleanQuery");
        
        LuceneEngine engine = LuceneEngine.getInstance();
        IndexSearcher searcher = new IndexSearcher(engine.getReader());

        TopDocs results;
        try {
            IKAnalyzer analyzer = new IKAnalyzer(true);
            TokenStream tokenStream = analyzer.tokenStream("content", key);
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            
            BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
            
            while (tokenStream.incrementToken()) {
                Term term = new Term("newsContent", charTermAttribute.toString());
                Query query = new TermQuery(term);
                booleanQueryBuilder.add(query, BooleanClause.Occur.SHOULD);
            }
            tokenStream.close();
            
            TermRangeQuery termRangeQuery = TermRangeQuery.newStringRange("qPublishdate", "20160101", "20161231", true, true);
            booleanQueryBuilder.add(termRangeQuery, BooleanClause.Occur.MUST);
            
            results = searcher.search(booleanQueryBuilder.build(), 1000);
        
            ScoreDoc[] hits = results.scoreDocs;
            int numTotalHits = results.totalHits;
            
            ResultList<QueryResult> resultList = new ResultList<QueryResult>();
            resultList.setCount(numTotalHits);
            
            List<QueryResult> list = new ArrayList<QueryResult>();
            
            for (int i = 0; i < hits.length; i++) {
                Document doc = searcher.doc(hits[i].doc);
                
                QueryResult result = new QueryResult();
                result.setId(doc.get("id"));
                result.setTitle(doc.get("title"));
                result.setAuthor(doc.get("author"));
                result.setCountry(doc.get("country"));
                result.setNewsSource(doc.get("newSource"));
                result.setPublishDate(doc.get("publishDate"));
                result.setqPublishdate(doc.get("qPublishdate"));
                result.setqPublishDataMonth(doc.get("qPublishDataMonth"));
                result.setNewsContent(doc.get("newsContent"));
                list.add(result);
            }
            resultList.setData(list);
    
            return resultList;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
