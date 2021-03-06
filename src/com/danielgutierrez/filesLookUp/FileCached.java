package com.danielgutierrez.filesLookUp;

import java.io.File;

public class FileCached implements Comparable<FileCached>{
	File file;
	long size;
	public FileCached(File file){
		this.file = file;
		this.size = file.length();}
	
	public FileCached(String filePath){
		this(new File(filePath));
	}
	public File getFile(){
		return this.file;
	}
	@Override
	public int compareTo(FileCached o) {
		return (int)(this.size - o.size);
	}
}
