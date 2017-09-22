package com.vega.springmvc.model;

import java.util.ArrayList;
import java.util.List;

public class MultiFileBucket {
	List<FileBucket> fileLists = new ArrayList<FileBucket>();
	
	
	public MultiFileBucket() {
		fileLists.add(new FileBucket());
		fileLists.add(new FileBucket());
		fileLists.add(new FileBucket());
		fileLists.add(new FileBucket());
	}
	public List<FileBucket> getFileLists() {
		return fileLists;
	}
	
	public void setFiles(List<FileBucket> fileLists) {
		this.fileLists = fileLists;
	}
}
