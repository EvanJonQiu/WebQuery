package com.esrisy.WebQuery.luceneEngine;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneEngine {
	
    private static final Logger logger = LoggerFactory.getLogger(LuceneEngine.class);
    
    // Path of testData
	static final String indexPath = "<path of testData>";
	private static LuceneEngine instance;
	private IndexReader reader;
	
	private LuceneEngine() {
	}
	
	public static LuceneEngine getInstance() {
	    logger.debug("LuceneEngine.getInstance");
		if (instance == null) {
			instance = new LuceneEngine();
			instance.init();
		}
		return instance;
	}
	
	public void init() {
	    logger.debug("LuceneEngine.init");
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public IndexReader getReader() {
	    logger.debug("LuceneEngine.getReader");
		return instance.reader;
	}

}
