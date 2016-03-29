package com.danielgutierrez.filesLookUp;

import java.io.File;
import java.text.DecimalFormat;

public class FileCached implements Comparable<FileCached>{
	File file;
	long size;
	String sizeStr;
	private boolean isChecked;
	
	public boolean isChecked() {
		return isChecked;
	}
	
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getSizeStr() {
		if(sizeStr == null)
			sizeStr = readableFileSize(size);
		return sizeStr;
	}
	
	public void setSizeStr(String sizeStr) {
		this.sizeStr = sizeStr;
	}

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
	
	public static String readableFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileCached other = (FileCached) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}
}
