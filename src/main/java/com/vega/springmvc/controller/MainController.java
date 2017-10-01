package com.vega.springmvc.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.vega.springmvc.model.FileBucket;
import com.vega.springmvc.model.MultiFileBucket;
import com.vega.springmvc.util.MultiFileValidator;

@Controller
public class MainController {
	// count amount file must upload
	private int amountFile = 0;
	// use this variables to sync fname in method GET to method POST
	private String mFileName;

	@Autowired
	MultiFileValidator multiFileValidator;

	@InitBinder("multiFileBucket")
	protected void initBinderMultiFileBucket(WebDataBinder data) {
		data.setValidator(multiFileValidator);
	}

	@GetMapping(value = "/fname={fName}&ackurl={ackUrl}&mkey={mKey}&orderid={orderId}&partnerid={partnerId}")
	public ModelAndView getData(@PathVariable(value = "fName") String fName,
			@PathVariable(value = "ackUrl") String ackUrl, @PathVariable(value = "mKey") String key,
			@PathVariable(value = "orderId") String orderId, @PathVariable(value = "partnerId") String partnerId) {
		// reference to file uploadFile.jsp
		ModelAndView model = new ModelAndView("uploadFile");
		model.addObject("titleFile", getFileName(fName));
		model.addObject("manFile", getMandatoryList(fName));
		mFileName = "" + fName;

		MultiFileBucket filesModel = new MultiFileBucket(amountFile);
		model.addObject("multiFileBucket", filesModel);
		return model;
	}

	// Todo: Post infor to database
	@PostMapping(value = "/upload")
	public ModelAndView multiFileUpload(@Valid MultiFileBucket multiFileBucket, BindingResult result)
			throws IOException {
		ModelAndView modelErrors = new ModelAndView("uploadFile");
		ModelAndView modelSuccess = new ModelAndView("uploadSuccess");
		if (result.hasErrors()) {
			System.out.println("validation errors in multi upload");
			String man[] = getMandatoryList(mFileName);

			/*
			 * i: index, count: count the mandatory file name equals 0
			 */
			int i = 0, count = 0;
			for (FileBucket file : multiFileBucket.getFiles()) {
				if (file.getFile() != null) {
					if (file.getFile().getSize() == 0) {
						if (man[i].equals("0")) {
							count++;
						} else if (man[i].equals("1")) {
							modelErrors.addObject("titleFile", getFileName(mFileName));
							modelErrors.addObject("manFile", getMandatoryList(mFileName));
							return modelErrors;
						}
					}
				}
				i++;
			}
			if (count == amountFile) {
				modelErrors.addObject("titleFile", getFileName(mFileName));
				modelErrors.addObject("manFile", getMandatoryList(mFileName));
				return modelErrors;
			} else {
				System.out.println("Fetching files");
				String fileNames[] = new String[amountFile - count];
				fetchingFile(multiFileBucket, fileNames);
				modelSuccess.addObject("fileNames", fileNames);
				return modelSuccess;
			}
		}
		System.out.println("Fetching files");
		String fileNames[] = new String[amountFile];
		fetchingFile(multiFileBucket, fileNames);
		modelSuccess.addObject("fileNames", fileNames);
		return modelSuccess;

	}

	// return attribute fname
	public String[] getFileName(String fileName) {
		String[] splitName = fileName.split(";");
		amountFile = splitName.length;
		for (int i = 0; i < splitName.length; i++) {
			splitName[i] = splitName[i].substring(0, splitName[i].length() - 2);
		}
		return splitName;
	}

	// return atribute mandatory
	public String[] getMandatoryList(String fileName) {
		String[] splitName = fileName.split(";");
		String[] tails = new String[splitName.length];

		amountFile = splitName.length;
		for (int i = 0; i < splitName.length; i++) {
			tails[i] = splitName[i].substring(splitName[i].indexOf("_") + 1);
		}
		return tails;
	}
	
	//Fetching file and save to server
	public void fetchingFile(MultiFileBucket multiFileBucket, String[] fileNames) throws IOException {
		int index = 0;
		for (FileBucket file : multiFileBucket.getFiles()) {
			if (file.getFile() != null) {
				if (file.getFile().getSize() == 0) {
					continue;
				}
			}
			byte[] bytes = file.getFile().getBytes();
			// Creating the directory to store file
			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath + File.separator + "file-store");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// Split file name and format of filename
			String fileFormat[] = file.getFile().getOriginalFilename().split("\\.");

			/*
			 * Current time upload To know time GMT you can follow link:
			 * http://www.fileformat.info/tip/java/date2millis.htm
			 */
			long currentTime = System.currentTimeMillis();
			// Create the file on server
			File serverFile = new File(dir.getAbsolutePath() + File.separator + fileFormat[0] + "-" + currentTime + "."
					+ fileFormat[fileFormat.length - 1]);
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
			stream.write(bytes);
			stream.close();

			/*
			 * Encrypt file name with format: name+ current time +format file
			 */
			fileNames[index] = "" + fileFormat[0] + "-" + currentTime + "." + fileFormat[fileFormat.length - 1];
			index++;
		}
	}
}
