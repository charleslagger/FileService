package com.vega.springmvc.service.infor;

import com.vega.springmvc.model.FintechFile;

public interface FintechFileService {
	public void addFile(FintechFile fintechFile);
	public FintechFile getFileByFileIdAndPartnerId(String fileName, String partnerId);
}
