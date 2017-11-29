package com.vega.springmvc.service.infor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vega.springmvc.dao.PartnerDao;
import com.vega.springmvc.model.Partner;
import com.vega.springmvc.service.infor.PartnerService;

/**
 * @author khoenv
 *
 */

@Service("partnerService")
@Transactional
public class PartnerServiceImpl implements PartnerService {	
	@Autowired
	private PartnerDao partnerDao;

	public void addPartner(Partner partner) {
		partnerDao.addPartner(partner);
	}
}
