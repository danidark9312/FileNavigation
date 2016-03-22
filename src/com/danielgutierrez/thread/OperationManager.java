
package com.danielgutierrez.thread;


import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.danielgutierrez.UI.MainFrame;
import com.danielgutierrez.UI.ResultScreen;
import com.danielgutierrez.workers.LogWorker;

public class OperationManager implements ThreadManager{

	private static OperationManager singleton;
	private static LinkedList<String> logStack;
	private static Date initTime;
	private static Date endTime;

	public static void addLogToStack(String log) {
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

	private OperationManager(){
		this.listFiles = new ArrayList();
		this.logStack = new LinkedList<String>();
		this.directoriesForProgress = new ArrayList<File>(30);
	}

	public void addFilesFromSelected(File[] files) {
		filesSelected.clear();
		for (File file : files) {
			filesSelected.add(new FileCached(file));
			logStack.push("file added" + file.getAbsolutePath());
		}
	}

	public void addFilesFromSelected(List<FileCached> files) {
		filesSelected = new LinkedList<FileCached>(files);
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
		logStackSize = logStackSize > maxBufferForLog ? (maxBufferForLog) : logStackSize;
		if (logStackSize > maxBufferForLog) {
			logStack.subList(logStackSize - maxBufferForLog, logStackSize);
			logStackSize = logStack.size();
		}
		buffer = (buffer == -1) ? logStackSize : buffer;
		String logLine = null;
		// System.out.println("logs pendientes: "+logStack.size());
		while ((logLine = logStack.poll()) != null && buffer-- != 0) {
			log(logLine);
		}
	}

	
	/**
	 * Metodo para extraer los posibles archivos iguales
	 * @return lista de archivos iguales
	 */
	
	public List<List<File>> extractCandidatesFiles() {
		initTime = new Date();
		writeFinished = false;
		LinkedList<FileCached> filesSelected = null;
		int threadsToDeploy =  Runtime.getRuntime().availableProcessors();
		logStack.addLast("creating: " + threadsToDeploy + " threads..");
		candidateGroup = splitListInSubList(listFiles, 100);
		logStack.addLast("files to compare: " + listFiles.size());
		logStack.addLast("subList size " + candidateGroup.size());
		updateProgress(candidateGroup.size(), 0);
		logStack.addLast("linea 126 ");
		System.out.println("126");
		System.out.println("127");
		if(this.filesSelected != null)
			filesSelected = new LinkedList<FileCached>(this.filesSelected);
		
		logStack.addLast("progress updated ");
		filesEqual = new ArrayList<List<FileCached>>();
		LookUpThread lookUpThread;
		LookUpThread.threadsAlive = threadsToDeploy;
		logStack.addLast("threads deployed: " + LookUpThread.threadsAlive);
		for (int i = 0; (i < threadsToDeploy); i++) {
			lookUpThread = new LookUpThread(candidateGroup, filesEqual, filesSelected);
			lookUpThread.setOnGroupThreadFinished(this);
			lookUpThread.setName("Searcher_" + i);
			lookUpThread.pickupFile();
		}
		
		// No finalizamos el metodo hasta que finalicen todos los threads
		
		while (LookUpThread.threadsAlive != 0 || !writeFinished) {
			try {
				System.out.println("sleep 1s: " + LookUpThread.threadsAlive);
				Thread.sleep(1000);
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

	private String getResult(File resultFile) {
		StringBuilder sb = new StringBuilder();
		DataInputStream bi = null;
		try {
			String temp = "";
			bi = new DataInputStream(new FileInputStream(resultFile));
			while ((temp = bi.readLine()) != null) {
				sb.append(temp+"\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bi.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public void initDialog(JProgressBar bar, JTextPane field) {
		this.progressBar = bar;
		this.log = field;
	}

	public void log(String text) {
		
		String textToShow = this.log.getText() + "\n" + text;
		textToShow = textToShow.length() > 2000 ? (textToShow.substring(textToShow.length() - 2000, textToShow.length())) : (textToShow);
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
				updateProgress(30, directoriesForProgress.size());
			}
		}
	}

	@Override
	public synchronized void onGroupThreadFinished() {
		/*
		 * System.out.println logStack.addLast("Thread " +
		 * Thread.currentThread().getName() + " finished");
		 */
		System.out.println("validating threads alive: " + LookUpThread.threadsAlive + " and write "+(writeFinished?"finished":"unfinished"));
		if (LookUpThread.threadsAlive == 0 && !writeFinished) {
			writeOnFileGroups(filesEqual);
			writeFinished = true;
		}
	}

	public void readFilesIntoList(File path) throws IOException {
		new LogWorker(this).execute();
		MainFrame.showDialog("Loading data...",false);
		path = (path == null) ? new File("") : path;
		File file = path;
		if (file.exists())
			addLogToStack("file does exists");
		else {
			addLogToStack("file not found");
			return;
		}
		DataInput output = new DataInputStream(new FileInputStream(file));
		String tempDir;
		try {
			listFiles.clear();
			while ((tempDir = output.readUTF()) != null) {
				listFiles.add(new FileCached(tempDir));
			}
			MainFrame.hideDialog();
			MainFrame.btnSaveResult.setEnabled(true);
			MainFrame.btnSearchSimilarFiles.setEnabled(true);
		} catch (EOFException e) {
			/* System.out.println */addLogToStack(listFiles.size() + " files read");
		}finally{
			LogWorker.turnoffLogFlag();
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
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				MainFrame.showDialog("sorting files...");
			}
		});
		LogWorker.turnoffLogFlag();
		sortFilesBySizeAsc();
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				MainFrame.hideDialog();
				MainFrame.lblFiles.setText(String.valueOf(listFiles.size()));
				JOptionPane.showMessageDialog(progressBar.getParent(), "Process finished successfull");
			}
		});
		this.isOpenProgressInfo = true;
	}

	public void setFilesSelected(List<FileCached> filesSelected) {
		this.filesSelected = new LinkedList<FileCached>(filesSelected);
	}

	public void setParameterCompare(File fileToSave, FileCached[] file) {
		if(file!=null){
			this.filesSelected = new LinkedList<FileCached>(Arrays.asList(file));
		}
		this.dirSaveResult = fileToSave;
	}

	public void setParameterScan(String baseDir, boolean appendToCurrent) {
		root = baseDir;
		this.appendToCurrent = appendToCurrent;
	}

	private void showResultScreen(File resultFile) {
		String result = getResult(resultFile);
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				ResultScreen.showResult(result);
			}
		});
	}
	private void showResultScreen(String result) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				ResultScreen.showResult(result);
			}
		});
	}

	public void sortFilesBySizeAsc() {
		System.out.println("Sorting file...");
		List<FileCached> filesCached = new ArrayList<>(listFiles.size());
		/*
		 * Comparator<File> comparator = new Comparator<File>() {
		 * @Override public int compare(File o1, File o2) { return
		 * (int)(o1.length() - o2.length()); } };
		 */
		System.out.println("antes de ordenar");
		Collections.sort(listFiles);
		System.out.println("files sorted");
	}

	private <T> LinkedList<LinkedList<T>> splitListInSubList(List<T> sourceList, int pieces) {
		LinkedList<LinkedList<T>> subLists = new LinkedList<LinkedList<T>>();
		if (sourceList.size() < pieces) {
			subLists.add(new LinkedList<T>(sourceList));
		} else {
			double sizeLists = Math.floor((double) sourceList.size() / pieces);
			for (int i = 0; i < pieces; i++) {
				LinkedList<T> subList = new LinkedList<T>();
				for (int k = ((int) (i * sizeLists)); k < ((i + 1) * sizeLists); k++) {
					subList.add(sourceList.get(k));
				}
				subLists.add(subList);
			}
		}
		return subLists;
	}

	public void updateProgress(int currentValue) {
		updateProgress(maxValueProgressBar, currentValue);
	}

	public void updateProgress(int maxValue, int currentValue) {
		this.maxValueProgressBar = maxValue;
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				int ScannedFolderProgress = (currentValue - maxValue) * (-1);
				int progress = 100 * ScannedFolderProgress / maxValue;
				progressBar.setValue(progress);
				progressBar.setString(progress + "%");
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
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				JOptionPane.showMessageDialog(progressBar.getParent(), "Process finished successfull");
			}
		});
	}

	private void writeOnFileGroups(List<List<FileCached>> candidateGroup) {
		logStack.addLast("preparing for saving results");
		File file = this.dirSaveResult;
		StringBuilder sb = new StringBuilder();
		logStack.addLast("escribiendo..: " + file.getAbsolutePath());
		BufferedWriter output = null;
		try {
			file.createNewFile();
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			String outputFileDir = null;
			for (List<FileCached> list : candidateGroup) {
				for (FileCached fileTmp : list) {
					outputFileDir = fileTmp.getFile().getAbsolutePath() + " : " + readableFileSize(fileTmp.size);
					
					output.write(outputFileDir);
					output.newLine();
					
					sb.append(outputFileDir);
					sb.append("\n");
				}
				sb.append("\n");
				output.newLine();
			}
			logStack.addLast("Escritura finalizada");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.close();
					showResultScreen(sb.toString());
					endTime = new Date();
					logStack.addLast("_time: "+((endTime.getTime()-initTime.getTime())/1000));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static String readableFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
}


