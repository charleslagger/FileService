package com.vega.springmvc.model;

import java.util.ArrayList;
import java.util.List;

public class MultiFileBucket {

	List<FileBucket> files = new ArrayList<FileBucket>();

	public MultiFileBucket() {
	}

	public MultiFileBucket(int index) {
		for (int i = 0; i < index; i++) {
			files.add(new FileBucket());
		}
	}

	public List<FileBucket> getFiles() {
		return files;
	}

	public void setFiles(List<FileBucket> files) {
		this.files = files;
	}
}
