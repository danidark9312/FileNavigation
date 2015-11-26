package com.danielgutierrez.filesLookUp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.server.LogStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LookUpThread implements Runnable{
	private int status = 0;
	public volatile static int threadsAlive = 0;
	private String threadName;
	private LinkedList<LinkedList<FileCached>> candidates;
	List<List<FileCached>> globalFilesEqual;
	private ThreadManager manager;
	private OperationManager operationManager;
	private LinkedList<FileCached> fileSelectedCompare;
	
	public void setName(String name){
		this.threadName = name;
	}
	
	public LookUpThread(LinkedList<LinkedList<FileCached>> candidates,List<List<FileCached>> filesEqual,LinkedList<FileCached> fileSelectedCompare){
		this.fileSelectedCompare = fileSelectedCompare;
		this.operationManager = OperationManager.getInstance();
		this.globalFilesEqual = filesEqual;
		this.candidates = candidates;
	}
	
	public void setOnGroupThreadFinished(ThreadManager tm){
		manager = tm;
	}
	
	public void compareBySelectedList(){
		
		System.out.println(this.threadName+": picking a new file");
		OperationManager.addLogToStack("Thread "+LookUpThread.this.threadName+" picking a new file");
		
		FileCached fileCached = fileSelectedCompare.poll();
		
		if(fileCached == null){
			pickupSelectedFile();
			return;
		}
		System.out.println("comparing file: "+fileCached.getFile().getAbsolutePath());
		
		operationManager.updateProgress(candidates.size());
		System.out.println("Thread: "+LookUpThread.this.threadName);
		
		
			List<FileCached> equals = new ArrayList<FileCached>();
			equals.add(fileCached);
			
			for(int i=0;i<candidates.size();i++){
				List<FileCached> files = candidates.get(i);
				for (FileCached fileCompare : files){
					if (!compareFileExtensions(fileCompare.getFile(), fileCached.getFile())) {
						continue;
					} else if (fileCompare.getFile().getName().equals(fileCached.getFile().getName())) {
						equals.add(fileCompare);
						files.remove(fileCompare);
						i--;
					} else if (fileCompare.size == fileCached.size && compareFilesByData(fileCompare.getFile(), fileCached.getFile())) {
						equals.add(fileCompare);
						files.remove(fileCompare);
						i--;
					}
				}
				
			}
			globalFilesEqual.add(equals);
		pickupFile();
		}
	public void compareAllByCandidates(){
		
		System.out.println(this.threadName+": picking a new file");
		OperationManager.addLogToStack("Thread "+LookUpThread.this.threadName+" picking a new file");
		
		LinkedList<FileCached> comparableList = candidates.poll();
		if(comparableList == null){
			pickupFile();
			return;
		}
		System.out.println("polling list "+comparableList.peek());
		
		operationManager.updateProgress(candidates.size());
		System.out.println("Thread: "+LookUpThread.this.threadName);
		FileCached fileCached = null;
		
		while((fileCached=comparableList.poll())!=null){
			List<FileCached> equals = new ArrayList<FileCached>();
			equals.add(fileCached);
			for(int i=0;i<comparableList.size();i++){
				FileCached fileCompare = comparableList.get(i);
				
				if(!compareFileExtensions(fileCompare.getFile(),fileCached.getFile())){
					continue;
				}
				else if(fileCompare.getFile().getName().equals(fileCached.getFile().getName())){
					equals.add(fileCompare);
					comparableList.remove(fileCompare);
					i--;
				}else if(fileCompare.size == fileCached.size && compareFilesByData(fileCompare.getFile(),fileCached.getFile())){
					equals.add(fileCompare);
					comparableList.remove(fileCompare);
					i--;
				}
				
			}
			globalFilesEqual.add(equals);
		}
		
		pickupFile();
	}
	
	@Override
	public void run() {
		if(fileSelectedCompare == null){
			compareAllByCandidates();
		}else{
			compareBySelectedList();
		}
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
	
//Obtenemos un archivo de la pila de candidatos
	public void pickupFile(){
		//En caso de que se halla utilizado el metodo para seleccionar determinados archivos, se llama otro metodo
		if(this.fileSelectedCompare != null){
			this.pickupSelectedFile();
			return;
		}
		if(candidates.size()>0){
			Thread tr = new Thread(this);
			tr.setName(this.threadName);
			tr.start();
		}else{
			System.out.println("finishing thread :"+threadName);
			threadsAlive--;
			OperationManager.addLogToStack("finish "+this.threadName+" thread");
			manager.onGroupThreadFinished();
		}
	}
	public void pickupSelectedFile(){
		if(this.fileSelectedCompare.size()>0){
			Thread tr = new Thread(this);
			tr.setName(this.threadName);
			tr.start();
		}else{
			System.out.println("finishing thread :"+threadName);
			threadsAlive--;
			OperationManager.addLogToStack("finish "+this.threadName+" thread");
			manager.onGroupThreadFinished();
		}
	}
	
}
