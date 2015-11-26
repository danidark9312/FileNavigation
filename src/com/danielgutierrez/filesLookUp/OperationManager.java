package com.danielgutierrez.filesLookUp;

import java.awt.Desktop;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import com.danielgutierrez.UI.MainFrame;
import com.danielgutierrez.workers.LogWorker;

public class OperationManager implements ThreadManager {
	private static OperationManager singleton;
	private static LinkedList<String> logStack;
	public static void addLogToStack(String log){
		logStack.addLast(log);
	}
	public static OperationManager getInstance() {
		if (singleton == null)
			singleton = new OperationManager();
		return singleton;
	}
	public static int getLogStackSize() {
		return logStack.size();
	}
	private List<FileCached> listFiles;
	private LinkedList<FileCached> filesSelected;
	private LinkedList<LinkedList<FileCached>> candidateGroup;
	private String root = "D://";
	private List<List<FileCached>> filesEqual;
	private JProgressBar progressBar;
	private JTextPane log;
	private boolean appendToCurrent;
	private File dirSaveResult;
	private boolean isOpenProgressInfo = true;
	List<File> directoriesForProgress;
	private int filesCount;
	private int maxValueProgressBar;
	
	private boolean writeFinished;
	
	private boolean openWrite;

	private int maxBufferForLog = 600;

	private OperationManager() {
		this.listFiles = new ArrayList();
		this.logStack = new LinkedList<String>();
		this.directoriesForProgress = new ArrayList<File>(30);
	}

	public void addFilesFromSelected(File[] files){
		filesSelected.clear();
		for(File file : files){
			filesSelected.add(new FileCached(file));
			logStack.push("file added" + file.getAbsolutePath());
		}
	}
	public void addFilesFromSelected(List<FileCached> files){
		filesSelected=new LinkedList<FileCached>(files);		
	}

	private void addFolderForProgress(File[] file) {
		if (this.directoriesForProgress.size() >= 30) {
			directoriesForProgress = directoriesForProgress.subList(0, 30);
			this.isOpenProgressInfo = false;
		}
		if (isOpenProgressInfo) {
			this.directoriesForProgress.addAll(Arrays.asList(file));
		}
	}

	public void dumpToLogStack() {
		dumpToLogStack(-1);
	}

	public void dumpToLogStack(int buffer) {
		int logStackSize = logStack.size();
		logStackSize = logStackSize>maxBufferForLog?(maxBufferForLog):logStackSize;
		if(logStackSize>maxBufferForLog){
			logStack.subList(logStackSize-maxBufferForLog, logStackSize);
			logStackSize = logStack.size();
		}
		
		
		buffer = (buffer == -1) ? logStackSize : buffer;
		String logLine = null;
		//System.out.println("logs pendientes: "+logStack.size());
		while ((logLine = logStack.poll()) != null && buffer-- != 0) {
			log(logLine);
		}
	}

	public List<List<File>> extractCandidatesFiles() {
		writeFinished = false;
		int threadsToDeploy = 1;//Runtime.getRuntime().availableProcessors();
		logStack.addLast("creating: " + threadsToDeploy + " threads..");
		candidateGroup = splitListInSubList(listFiles, 100);
		logStack.addLast("files to compare: "+listFiles.size());
		logStack.addLast("subList size " + candidateGroup.size());
		
		updateProgress(candidateGroup.size(), 0);
		logStack.addLast("linea 119 ");
		LinkedList<FileCached> filesSelected = new LinkedList<FileCached>(this.filesSelected);
		logStack.addLast("progress updated ");
		
		filesEqual = new ArrayList<List<FileCached>>();
		LookUpThread lookUpThread;
		LookUpThread.threadsAlive = threadsToDeploy;
		logStack.addLast("theads deployed: "+LookUpThread.threadsAlive);
		for (int i = 0; (i < threadsToDeploy) ; i++) {
			lookUpThread = new LookUpThread(candidateGroup, filesEqual,filesSelected);
			lookUpThread.setOnGroupThreadFinished(this);
			lookUpThread.setName("Searcher_"+i);
			lookUpThread.pickupFile();
		}
		// No finalizamos el metodo hasta que finalicen todos los threads
		while (LookUpThread.threadsAlive != 0 || !writeFinished) {
			try {
				System.out.println("sleep 5s: "+LookUpThread.threadsAlive);
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("no more sleep");
		
		return null;
	}

	public List<FileCached> extractFileList() {
		return listFiles;
	}

	/*
	 * public static void main(String args[]){ OperationManager main = new
	 * OperationManager(); main.lookupFiles(null); List<File> files =
	 * main.extractFileList();
	 * System.out.println("end: "+files.size()+" files"); try {
	 * main.writeFilesIntoFile(null); } catch (IOException e) {
	 * e.printStackTrace(); } }
	 */
	public String getBaseDir() {
		return this.root;
	}

	public List<FileCached> getFilesSelected() {
		return filesSelected;
	}

	

	private File[] getFolderInFile(File base) {
		List<File> folders = new ArrayList<File>();
		File filetmp;
		for (String file : base.list()) {
			filetmp = new File(base.getAbsolutePath() + File.separator + file);
			if (filetmp.isDirectory())
				folders.add(filetmp);
		}
		
		return folders.toArray(new File[0]);
	}

	public void initDialog(JProgressBar bar, JTextPane field) {
		this.progressBar = bar;
		this.log = field;
	}
	
	
	
	public void log(String text) {
		String textToShow = this.log.getText() + "\n" + text;
		textToShow = textToShow.length()>2000?(textToShow.substring(textToShow.length()-2000,textToShow.length())):(textToShow);
		this.log.setText(textToShow);
	}
	private void lookupFiles(String parent) {
		File rootFile = new File(parent == null ? root : parent);
		File initList[] = rootFile.listFiles();
		// Collections.addAll(listFiles, initList);
		for (File file : initList) {
			if (!file.isDirectory()) {
				listFiles.add(new FileCached(file));
			}
		}
		File[] folders = getFolderInFile(rootFile);
		addFolderForProgress(folders);
		for (File file : folders) {
			logStack.push("scanning folder: " + file.getAbsolutePath());
			if (file.list() != null && file.list().length > 0) {
				lookupFiles(file.getAbsolutePath());
			}
			if (directoriesForProgress.remove(file)) {
				updateProgress(30,directoriesForProgress.size());
			}
		}
	}
	@Override
	public synchronized void onGroupThreadFinished() {
		/* System.out.println logStack.addLast("Thread " + Thread.currentThread().getName() + " finished");*/
		System.out.println("validating threads alive: "+LookUpThread.threadsAlive+ " and write finish: "+writeFinished);
		if (LookUpThread.threadsAlive == 0 && !writeFinished) {
			writeOnFileGroups(filesEqual);
			writeFinished = true;
		}
	}

	public void readFilesIntoList(File path) throws IOException {
		path = (path == null) ? new File("") : path;
		File file = path;
		if (file.exists())
			log("file does exists");
		else {
			log("file not found");
			return;
		}
		DataInput output = new DataInputStream(new FileInputStream(file));
		String tempDir;
		try {
			listFiles.clear();
			while ((tempDir = output.readUTF()) != null) {
				listFiles.add(new FileCached(tempDir));
			}
		} catch (EOFException e) {
			/* System.out.println */log(listFiles.size() + " files read");
		}
	}

	public void scanNow(String parent, boolean append, SwingWorker worker) {
		System.out.println("starting scan ..");
		if (!append) {
			listFiles.clear();
		}
		lookupFiles(parent);
		logStack.addLast("files found: " + listFiles.size());
		System.out.println("folders for progress " + directoriesForProgress.size());
		System.out.println("scan finished");
		logStack.addLast("scan finished");
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainFrame.showDialog("sorting files...");
			}
		});
		
		LogWorker.turnoffLogFlag();
		
		sortFilesBySizeAsc();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainFrame.hideDialog();
				MainFrame.lblFiles.setText(String.valueOf(listFiles.size()));
				JOptionPane.showMessageDialog(progressBar.getParent(), "Proccess finished successfull");
			}
		});
		this.isOpenProgressInfo = true;
	}

	public void setFilesSelected(List<FileCached> filesSelected) {
		this.filesSelected = new LinkedList<FileCached>(filesSelected);
	}

	
	
	public void setParameterCompare(File fileToSave,FileCached[] file) {
		this.filesSelected = new LinkedList<FileCached>(Arrays.asList(file));
		this.dirSaveResult = fileToSave;
	}

	
	
	public void setParameterScan(String baseDir, boolean appendToCurrent) {
		root = baseDir;
		this.appendToCurrent = appendToCurrent;
	}

	public void sortFilesBySizeAsc() {
		System.out.println("Sorting file...");
		
		List<FileCached> filesCached = new ArrayList<>(listFiles.size());
		
		
		
/*		Comparator<File> comparator = new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return (int)(o1.length() - o2.length());
			}
		};*/
		
		System.out.println("antes de ordenar");
		Collections.sort(listFiles);
		
		System.out.println("files sorted");
	}

	private <T> LinkedList<LinkedList<T>> splitListInSubList(List<T> sourceList, int pieces) {
		LinkedList<LinkedList<T>> subLists = new LinkedList();
		double sizeLists = Math.floor((double) sourceList.size() / pieces);
		for (int i = 0; i < pieces; i++) {
			LinkedList<T> subList = new LinkedList<T>();
			for (int k = ((int) (i * sizeLists)); k < ((i + 1) * sizeLists); k++) {
				subList.add(sourceList.get(k));
			}
			subLists.add(subList);
		}
		return subLists;
	}
	
	

	public void updateProgress(int currentValue) {
		updateProgress(maxValueProgressBar,currentValue);
		
	}

	public void updateProgress(int maxValue,int currentValue) {
		this.maxValueProgressBar = maxValue;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				int ScannedFolderProgress = (currentValue - maxValue) * (-1);
				int progress = 100 * ScannedFolderProgress / maxValue;
				progressBar.setValue(progress);
				progressBar.setString(progress+"%");
					
			}
		});
	}

	public void writeFilesIntoFile(File path) throws IOException {
		path = (path == null) ? new File("") : path;
		File file = path;
		System.out.println(file.getAbsolutePath());
		file.createNewFile();
		DataOutput output = new DataOutputStream(new FileOutputStream(file));
		System.out.println("starting to write.. " + listFiles.size() + " files");
		for (FileCached fileTmp : listFiles) {
			output.writeUTF(fileTmp.getFile().getAbsolutePath());;
		}
		System.out.println("write finished..");
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(progressBar.getParent(), "Proccess finished successfull");
			}
		});
	}

	private void writeOnFileGroups(List<List<FileCached>> candidateGroup) {
		logStack.addLast("preparing for saving results");
		File file = this.dirSaveResult;
		logStack.addLast("escribiendo..: " + file.getAbsolutePath());
		BufferedWriter output = null;
		try {
			file.createNewFile();
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for (List<FileCached> list : candidateGroup) {
				for (FileCached fileTmp : list) {
					output.write(fileTmp.getFile().getAbsolutePath() + " : " + fileTmp.size);
					output.newLine();
				}
				output.newLine();
			}
			logStack.addLast("Escritura finalizada");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
