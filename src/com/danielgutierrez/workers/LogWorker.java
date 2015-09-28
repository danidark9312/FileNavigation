package com.danielgutierrez.workers;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.danielgutierrez.filesLookUp.OperationManager;

public class LogWorker<T, V> extends SwingWorker<Object,Object> {
	OperationManager manager; 
	private static boolean flagWriteLog = true; 
	
	public static final int OPERATION_SCAN = 1;
	public static final int OPERATION_COMPARE = 2;
	
	
	
	
	public static void turnonLogFlag(){
		flagWriteLog = true;
	}
	
	public static void turnoffLogFlag(){
		flagWriteLog = false;
	}
	
	public LogWorker(OperationManager manager) {
		this.manager = manager;
	}
	

	@Override
	protected T doInBackground() throws Exception {
		while(flagWriteLog || OperationManager.getLogStackSize()==0){
			publish(new Object());
			Thread.sleep(50);
		}
		
		return null;
				
	}
	
	
	
	@Override
	protected void process(List<Object> v){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				manager.dumpToLogStack(10);
				
			}
		});
				
	}

}


