package com.vega.springmvc.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.vega.springmvc.service.infor.FintechFileService;

@Controller
public class ViewController {
	private Logger logger = Logger.getLogger(ViewController.class);
	
	@Autowired
	private FintechFileService fintechFileService;

	@GetMapping(value = "/content_file")
	public ModelAndView contentFile(HttpServletResponse response, @RequestParam(value = "file_id") String fileId,
			@RequestParam("partner_id") String partnerId) throws IOException {
		logger.info("get Content File");
		String path = fintechFileService.getFileByFileIdAndPartnerId(fileId, partnerId).getPath();
		logger.info("Path: " + path);
		File file = new File(path);

		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		if (mimeType == null) {
			logger.info("mimetype is not detectable, will take default");
			mimeType = "application/octet-stream";
		}

		logger.info("Mimetype: " + mimeType);

		ModelAndView model = new ModelAndView("contentFile");
		model.addObject("fileName", fileId);
		model.addObject("partnerId", partnerId);
		model.addObject("type", mimeType);
		
		return model;
	}

	@GetMapping(value = "/download-file/{fileName}/{partnerId}")
	public void downloadFile(HttpServletResponse response, @PathVariable(value = "fileName") String fileName,
			@PathVariable(value = "partnerId") String partnerId) throws IOException {
		logger.info("Download File");
		String path = fintechFileService.getFileByFileIdAndPartnerId(fileName, partnerId).getPath();
		logger.info("Path: " + path);
		File file = new File(path);

		String mimeType = "application/octet-stream";

		logger.info("mimetype : " + mimeType);

		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
		response.setContentLength((int) file.length());

		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		FileCopyUtils.copy(inputStream, response.getOutputStream());
	}

	@GetMapping(value = "/view-file/{fileName}/{partnerId}")
	public void viewFile(HttpServletResponse response, @PathVariable(value = "fileName") String fileName,
			@PathVariable(value = "partnerId") String partnerId) throws IOException {
		logger.info("View file");
		String path = fintechFileService.getFileByFileIdAndPartnerId(fileName, partnerId).getPath();
		logger.info("Path: " + path);
		File file = new File(path);

		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		if (mimeType == null) {
			logger.info("mimetype is not detectable, will take default");
			mimeType = "application/octet-stream";
		}

		logger.info("mimetype : " + mimeType);

		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
		response.setContentLength((int) file.length());

		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		FileCopyUtils.copy(inputStream, response.getOutputStream());
	}
}

