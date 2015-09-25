package com.danielgutierrez.filesLookUp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LookUpThread implements Runnable{
	private int status = 0;
	public static int threadsAlive = 0;
	private String threadName;
	private LinkedList<LinkedList<File>> candidates;
	List<List<File>> globalFilesEqual;
	private ThreadManager manager;
	
	public LookUpThread(LinkedList<LinkedList<File>> candidates,List<List<File>> filesEqual){
		threadsAlive++;
		this.globalFilesEqual = filesEqual;
		this.candidates = candidates;
	}
	
	public void setOnGroupThreadFinished(ThreadManager tm){
		manager = tm;
	}
	
	@Override
	public void run() {
		LinkedList<File> comparableList = candidates.poll();
		System.out.println("Thread: "+Thread.currentThread().getName()+" working on the list "+comparableList.toString().substring(0, 200));
		if(comparableList == null)
			return;
		File file = null;
		
		while((file=comparableList.poll())!=null){
			List<File> equals = new ArrayList<File>();
			equals.add(file);
			for(int i=0;i<comparableList.size();i++){
				File fileCompare = comparableList.get(i);
				
				if(!compareFileExtensions(fileCompare,file)){
					continue;
				}
				else if(fileCompare.getName().equals(file.getName())){
					equals.add(fileCompare);
					comparableList.remove(fileCompare);
					i--;
				}else if(fileCompare.length() == file.length() && compareFilesByData(fileCompare,file)){
					equals.add(fileCompare);
					comparableList.remove(fileCompare);
					i--;
				}
				
			}
			globalFilesEqual.add(equals);
		}
		
		pickupFile();
		}
	
	
private boolean compareFileExtensions(File file1,File file2){
	if(file1.getName().lastIndexOf(".") == -1 || file2.getName().lastIndexOf(".")== -1)
		return false;
	String ext1 = file1.getName().substring(file1.getName().lastIndexOf("."));
	String ext2 = file2.getName().substring(file2.getName().lastIndexOf("."));
	return ext1.equals(ext2);
	
}
private boolean compareFilesByData(File file1,File file2){
	boolean equals = true;
	FileInputStream fos1 = null;
	FileInputStream fos2 = null;
	try{
	fos1 = new FileInputStream(file1);
	fos2 = new FileInputStream(file2);
	
	byte buffer1[] = new byte[2000000];
	byte buffer2[] = new byte[2000000];
	
	fos1.read(buffer1);
	fos2.read(buffer2);
	
	for(int i=0;i<buffer1.length;i++){
		if(buffer1[i]!=buffer2[i]){
			equals = false;
			break;
		}
	}
	}catch(IOException e){
		return false;
	}finally{
		try{
			if(fos1 != null && fos2!=null){
				fos1.close();
				fos2.close();	
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	return equals;
	
}
	
	public void pickupFile(){
		if(candidates.size()>0){
			Thread tr = new Thread(this);
			tr.start();
		}else{
			this.threadsAlive--;
			manager.onGroupThreadFinished();
		}
	}
	
}
