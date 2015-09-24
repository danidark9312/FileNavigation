package com.danielgutierrez.FilesLookUp;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Main implements ThreadManager{
	private List<File> listFiles;
	private LinkedList<LinkedList<File>> candidateGroup;
	private String root = "D://";
	private List<List<File>> filesEqual;
	
	public static void main(String args[]){
		Main main = new Main();
		main.lookupFiles(null);
		List<File> files = main.extractFileList();
		System.out.println("end: "+files.size()+" files");
		try {
			main.writeFilesIntoFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void writeFilesIntoFile() throws IOException{
		File file = new File("listFiles.dat");
		System.out.println(file.getAbsolutePath());
			file.createNewFile();
		DataOutput output = new DataOutputStream(new FileOutputStream(file));
		sortFilesBySizeAsc();
		System.out.println("starting to write..");
		for(File fileTmp : listFiles){
			output.writeUTF(fileTmp.getAbsolutePath());;
		}
		System.out.println("write finished..");
		
	}
	
	public void readFilesIntoList() throws IOException{
		File file = new File("listFiles.dat");
		
		DataInput output = new DataInputStream(new FileInputStream(file));
		
		String tempDir;
		try{
			while((tempDir = output.readUTF()) != null){
				listFiles.add(new File(tempDir));
			}	
		}catch(EOFException e){
			System.out.println(listFiles.size()+" dir read");	
		}
	}
	
	
	public void sortFilesBySizeAsc(){
		System.out.println("Sorting file...");
		Comparator<File> comparator = new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				long difference = o1.length() - o2.length();
				int res = 0;
				if (difference > 0)
					res = 1;
				else if (difference < 0)
					res = -1;
				return res;
			}
		};
		Collections.sort(listFiles, comparator);
		System.out.println("files sorted");
	}
	
	private <T>LinkedList<LinkedList<T>> splitListInSubList(List<T> sourceList,int pieces){
		LinkedList<LinkedList<T>> subLists = new LinkedList();
		
		double sizeLists = Math.floor((double)sourceList.size()/pieces);
		
		for(int i=0;i<pieces;i++){
			LinkedList<T> subList = new LinkedList<T>();
			for(int k=((int)(i*sizeLists)); k<((i+1)*sizeLists);k++){
					subList.add(sourceList.get(k));
			}
			subLists.add(subList);
		}
		
		return subLists;
	}
	
	public List<List<File>> extractCandidatesFiles(){
		int threadsToDeploy = Runtime.getRuntime().availableProcessors()*2;
		System.out.println("creating: "+threadsToDeploy+" threads..");
		
		//candidateGroup = new ArrayList<List<File>>();
		candidateGroup = splitListInSubList(listFiles,100);
		
		//LinkedList<File> linkedFile = new LinkedList<File>(listFiles);
		filesEqual = new ArrayList<List<File>>();
		LookUpThread lookUpThread;
		for(int i = 0;i<threadsToDeploy;i++){
			lookUpThread = new LookUpThread( candidateGroup, filesEqual);
			lookUpThread.setOnGroupThreadFinished(this);
			lookUpThread.pickupFile();
			
		}
			
		while(LookUpThread.threadsAlive != 0){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return /*candidateGroup*/null;
	}
	
	
	@Override
	public void onGroupThreadFinished() {
		System.out.println("Thread "+Thread.currentThread().getId()+" finished");
		if(LookUpThread.threadsAlive == 0){
			writeOnFileGroups(filesEqual);
		}
	}
	
	private void writeOnFileGroups(List<List<File>> candidateGroup){
		System.out.println("preparing for writing on file");
		File file = new File("candidates groups.dat");
		System.out.println(file.getAbsolutePath());
		BufferedWriter output = null;
		try{
		file.createNewFile();
		output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		for(List<File> list : candidateGroup){
		for(File fileTmp : list){
			output.write(fileTmp.getAbsolutePath() + " : "+fileTmp.length());
			output.newLine();
		}
		output.newLine();
		}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {
				if(output!=null){
					output.close();	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Main(){
		this.listFiles = new ArrayList();
	}
	
	public List<File> extractFileList(){
		return listFiles;
	}
	
	public void lookupFiles(String parent) {
		File rootFile = new File(parent == null ? root : parent);
		File initList[] = rootFile.listFiles();

		//Collections.addAll(listFiles, initList);
		for(File file : initList){
			if(!file.isDirectory()){
				listFiles.add(file);	
			}
		}
		File[] folders = getFolderInFile(rootFile);

		for (File file : folders) {
			if (file.list()!=null && file.list().length > 0) {
				lookupFiles(file.getAbsolutePath());
			}
		}

	}
	private File[] getFolderInFile(File base){
		List<File> folders = new ArrayList<File>();
		File filetmp;
		for(String file : base.list()){
			filetmp = new File(base.getAbsolutePath()+File.separator+file);
			if(filetmp.isDirectory())
				folders.add(filetmp);
		}
		
		return  folders.toArray(new File[0]);
	}

	

}
