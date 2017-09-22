package com.vega.springmvc.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import com.vega.springmvc.model.FileBucket;
import com.vega.springmvc.util.FileValidator;
import com.vega.springmvc.util.MultiFilesValidator;

@Controller
public class TestController {
	@Autowired
	FileValidator fileValidator;
	
	@Autowired
	MultiFilesValidator multiFileValidator;
	
	
	@InitBinder("fileBucket")
	protected void initBinderFileBucket(WebDataBinder data) {
		data.setValidator(fileValidator);
	}
	@InitBinder("multiFilesBucket")
	protected void initBinderMultiFilesBucket(WebDataBinder data) {
		data.setValidator(multiFileValidator);
	}
	
	@GetMapping("/welcome")
	public String welcome() {
		return "welcome";
	}
	
	@GetMapping("/uploadSingleFile")
	public String uploadSingleFile(ModelMap model) {
		FileBucket fileBucket = new FileBucket();
		model.addAttribute("fileBucket", fileBucket);
		return "uploadFile";
	}
	
	@PostMapping("/uploadSingleFile")
	public String singleFileUpload(@Valid FileBucket fileBucket,
			BindingResult result, Model model) throws IOException{
		if(result.hasErrors()) {
			System.out.println("Please try again!");
			return "uploadFile";
		}else {
			System.out.println("Fetching file");
			byte[] bytes = fileBucket.getFile().getBytes();

			// Creating the directory to store file
			String rootPath = System.getProperty("catalina.home");
			File dir = new File(rootPath + File.separator + "uploadTemp");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			// Create the file on server
			File serverFile = new File(dir.getAbsolutePath() + File.separator + fileBucket.getFile().getOriginalFilename());
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
			stream.write(bytes);
			stream.close();
			
			model.addAttribute("fileName", fileBucket.getFile().getOriginalFilename() );
			
			return "success";
		}
	}
	
}
