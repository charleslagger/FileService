//package com.vega.springmvc.util;
//
//import org.springframework.stereotype.Component;
//import org.springframework.validation.Errors;
//import org.springframework.validation.Validator;
//
//import com.vega.springmvc.model.FileBucket;
//
//@Component
//public class FileValidator implements Validator{
//	public void validate(Object arg0, Errors arg1) {
//		FileBucket file = (FileBucket) arg0;
//		if(file.getFile() != null) {
//			if(file.getFile().getSize() == 0) {
//				arg1.rejectValue("file", "missing.file");
//			}
//		}
//		
//	}
//	
//	public boolean supports(Class<?> arg0) {
//		return FileBucket.class.isAssignableFrom(arg0);
//	}
//}
