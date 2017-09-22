package com.vega.springmvc.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.vega.springmvc.model.FileBucket;
import com.vega.springmvc.model.MultiFileBucket;

@Component
public class MultiFilesValidator implements Validator{
	public boolean supports(Class<?> arg0) {
		return MultiFileBucket.class.isAssignableFrom(arg0);
	}
	
	public void validate(Object arg0, Errors error) {
		MultiFileBucket multiFileBucket = (MultiFileBucket) arg0;
		int index = 0;
		for(FileBucket file: multiFileBucket.getFileLists()) {
			if(file.getFile()!=null) {
				if(file.getFile().getSize() == 0) {
					error.rejectValue("file[" + index + "].file", "missing.file");
				}
			}
			index++;
		}
	}
}
