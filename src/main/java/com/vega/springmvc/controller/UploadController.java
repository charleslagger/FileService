package com.vega.springmvc.controller;

import java.io.IOException;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import com.vega.springmvc.model.MultiFileBucket;
import com.vega.springmvc.model.UrlParam;
import com.vega.springmvc.service.upload.UploadService;
import com.vega.springmvc.util.JsonUtil;
import com.vega.springmvc.util.MultiFileValidator;

@Controller
public class UploadController {	
	private Logger logger = Logger.getLogger(UploadController.class);
	private Base64 base64 = new Base64();
	private UrlParam urlParam = new UrlParam();
	@Autowired
	private JsonUtil jsonUtil;
	
	@Autowired
	private MultiFileValidator multiFileValidator;

	@InitBinder("multiFileBucket")
	private void initBinderMultiFileBucket(WebDataBinder data) {
		data.setValidator(multiFileValidator);
	}
	
	@Autowired
	private UploadService uploadService;

	@GetMapping(value = "/{urlReceive}")
	public ModelAndView getData(@PathVariable(value = "urlReceive") String urlReceive) {
		urlReceive = new String(base64.decode(urlReceive.getBytes()));

		uploadService.getInfo(urlParam, urlReceive);
		logger.info("UrlParams: " + jsonUtil.objectToJSon(urlParam));

		ModelAndView model = new ModelAndView("uploadFile");
		model.addObject("doctypeField", uploadService.getFileName(urlParam, urlParam.getfName()));
		model.addObject("requiredField", uploadService.getTailsList(urlParam, urlParam.getfName()));

		MultiFileBucket filesModel = new MultiFileBucket(urlParam.getAmountFile());
		model.addObject("multiFileBucket", filesModel);
		
		return model;
	}

	@PostMapping(value = "/upload")
	public ModelAndView multiFileUpload(@Valid MultiFileBucket multiFileBucket, BindingResult result)
			throws IOException {
		ModelAndView modelErrors = new ModelAndView("uploadFile");
		ModelAndView modelSuccess = new ModelAndView("uploadSuccess");

		if (result.hasErrors()) {
			logger.info("validation errors in multi upload");
			uploadService.uploadMadatoryFile(multiFileBucket, modelSuccess, modelErrors, urlParam);
		}

		uploadService.uploadAllFile(multiFileBucket, modelSuccess, modelErrors, urlParam);
		
		return modelSuccess;
	}
}
