package com.vega.springmvc.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.vega.springmvc.model.FileBucket;
import com.vega.springmvc.model.MultiFileBucket;

@Component
public class MultiFileValidator implements Validator {	
	public boolean supports(Class<?> clazz) {
		return MultiFileBucket.class.isAssignableFrom(clazz);
	}

	//TODO: show error in optional choose file
	public void validate(Object obj, Errors errors) {
		MultiFileBucket multiBucket = (MultiFileBucket) obj;
		
		int index=0;
		
		for(FileBucket file : multiBucket.getFiles()){
			String fileFormat[] = file.getFile().getOriginalFilename().split("\\.");
			if(file.getFile()!=null){
				if (file.getFile().getSize() == 0) {
					errors.rejectValue("files["+index+"].file", "missingMandatory.file");
				}/*else if(file.getFile().getSize() == 0){
					errors.rejectValue("files["+index+"].file", "missingOptional.file");
				}*/
			}

			boolean checkValidFile = fileFormat[fileFormat.length - 1].equals("exe")
					|| fileFormat[fileFormat.length - 1].equals("php")
					|| fileFormat[fileFormat.length - 1].equals("sh")
					|| fileFormat[fileFormat.length - 1].equals("com");

			if (checkValidFile) {
				System.out.println("Prevent some files mandatory.");
				errors.rejectValue("files["+index+"].file", "invalid.file");
			}
			index++;
		}
		
	}
}