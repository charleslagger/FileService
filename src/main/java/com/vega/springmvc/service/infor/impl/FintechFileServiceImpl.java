package com.vega.springmvc.service.infor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vega.springmvc.dao.FintechFileDao;
import com.vega.springmvc.model.FintechFile;
import com.vega.springmvc.service.infor.FintechFileService;

@Service("fintechFileService")
@Transactional
public class FintechFileServiceImpl implements FintechFileService {

	@Autowired
	FintechFileDao fintechFileDao;

	public void addFile(FintechFile fintechFile) {
		fintechFileDao.addFile(fintechFile);
	}

	public FintechFile getFileByFileIdAndPartnerId(String fileName, String partnerId) {
		return fintechFileDao.getFileByFileIdAndPartnerId(fileName, partnerId);
	}
}
