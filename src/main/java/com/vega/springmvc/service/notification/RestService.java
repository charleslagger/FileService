package com.vega.springmvc.service.notification;

import java.util.List;

import com.vega.springmvc.model.LoanDocument;
import com.vega.springmvc.service.transform.CoreResponse;

/**
 * @author khoenv
 *
 */

public interface RestService {
	public CoreResponse sendGet(String ackUrl, Object... objects);

	public CoreResponse sendPost(String ackUrl, List<LoanDocument> loanDocuments);
}
