package com.vega.springmvc.dao;

import com.vega.springmvc.model.FintechFile;

public interface FintechFileDao {
	public void addFile(FintechFile fintechFile);
	public FintechFile getFileByFileIdAndPartnerId(String fileName, String partnerId);
}
