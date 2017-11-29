package com.vega.springmvc.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.vega.springmvc.dao.FintechFileDao;
import com.vega.springmvc.model.FintechFile;

@Repository
public class FintechFileDaoImpl implements FintechFileDao {
	protected static Logger logger = Logger.getLogger("FintechFileService");
	@Autowired
	private MongoTemplate mongoTemplate;

	public void addFile(FintechFile fintechFile) {
		logger.info("add new fintech files");
		BasicDBObject documentFiles = new BasicDBObject();
		documentFiles.put("FileName", fintechFile.getFileName());

		documentFiles.put("Path", fintechFile.getPath());
		documentFiles.put("DateCreated", "" + fintechFile.getDateCreated());
		// TODO: Content field in Mongo
		documentFiles.put("Content", fintechFile.getContent());
		documentFiles.put("OrderId", fintechFile.getOrderId());
		documentFiles.put("PartnerId", fintechFile.getPartnerId());
		mongoTemplate.getCollection("FintechFiles").insert(documentFiles);
	}

	public FintechFile getFileByFileIdAndPartnerId(String fileName, String partnerId) {
		logger.info("take file infor have file name: " + fileName + "& partnerId: " + partnerId);

		Query query = new Query();
		query.addCriteria(Criteria.where("fileName").is(fileName).and("partnerId").is(partnerId));
		FintechFile fintechFile = mongoTemplate.findOne(query, FintechFile.class, "FintechFiles");
		if (fintechFile == null) {
			logger.info("Don't have file documents in db");
			return null;
		}

		logger.info("Fintech File path: " + fintechFile.getPath());
		return fintechFile;
	}

}
