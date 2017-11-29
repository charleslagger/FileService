package com.vega.springmvc.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.vega.springmvc.dao.PartnerDao;
import com.vega.springmvc.model.Partner;

@Repository
public class PartnerDaoImpl implements PartnerDao{
	@Autowired
	private MongoTemplate mongoTemplate;
	protected static Logger logger = Logger.getLogger("PartnerService");

	public void addPartner(Partner partner) {
		logger.info("Adding a new partner...");

		BasicDBObject query = new BasicDBObject();
		String namePartner = partner.getName();
		logger.info("--->>>Name partner: " + namePartner);
		query.put("Name", namePartner);
		DBCursor cursor = mongoTemplate.getCollection("Partners").find(query);

		// Kiem tra neu partnerId da co trong DB thi ko lam gi, neu khong co thi tao moi
		if (cursor.hasNext() == false) {
			query.put("Name", partner.getName());
			query.put("IP", partner.getIp());
			query.put("AuthKey", partner.getAuthenKey());
			query.put("Status", partner.getStatus());
			mongoTemplate.getCollection("Partners").insert(query);
		}else {
			logger.info("Adding false because partner had existed.");
		}
	}

}
