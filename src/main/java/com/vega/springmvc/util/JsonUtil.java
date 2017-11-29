package com.vega.springmvc.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component("jsonUtil")
public class JsonUtil {
	@Autowired
	private Gson gson;
	public String objectToJSon(Object obj) {
		String jsonStr = gson.toJson(obj);
		return jsonStr;
	}
}
