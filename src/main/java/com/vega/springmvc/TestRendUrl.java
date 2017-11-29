package com.vega.springmvc;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class TestRendUrl {
	public static void main(String[] args) {
		Logger logger = Logger.getLogger(TestRendUrl.class);
//		String url = "fname=contract_document_id_1;cmt_0&ackurl=/private/contracts/acknowledge-uploaded&mkey=samo@1234&orderid=3&opt=1&partnerid=samo@12345";
		String url = "fname=name_1;test12_1;Test3_1&ackurl=/private/loan-document/acknowledge-uploaded&mkey=samo@1234&orderid=1&opt=0&partnerid=samo@1234";
		Base64 base64 =new Base64();
		url = new String(base64.encode(url.getBytes()));
		logger.info("url encode: " + url);
	}
}
