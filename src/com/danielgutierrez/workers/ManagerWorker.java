package com.danielgutierrez.workers;

import java.util.Arrays;
import java.util.List;
import javax.swing.SwingWorker;
import com.danielgutierrez.UI.MainFrame;
import com.danielgutierrez.filesLookUp.OperationManager;

public class ManagerWorker<T, V> extends SwingWorker<Object,Object> {
	OperationManager manager; 
	
	private int operationToDo;
	
	public static final int OPERATION_SCAN = 1;
	public static final int OPERATION_COMPARE = 2;
	
	
	public ManagerWorker(OperationManager manager,int operationToDo) {
		this.operationToDo = operationToDo;
		this.manager = manager;
	}
	

	@Override
	protected T doInBackground() throws Exception {
		switch (operationToDo) {
		case OPERATION_SCAN:
			manager.scanNow(manager.getBaseDir(), false, this);
			break;
		case OPERATION_COMPARE:
			if (manager.getFilesSelected() != null) {
				manager.addFilesFromSelected(manager.getFilesSelected());
			}
			try{
				manager.extractCandidatesFiles();
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println("turning off the log");
			LogWorker.turnoffLogFlag();
			break;
		default:
			break;
		}
		
		return null;
				
	}
	
	
	
	@Override
	protected void process(List<Object> v){
		manager.dumpToLogStack(20);		
	}
	
	@Override
	protected void done(){
		MainFrame.btnSaveResult.setEnabled(true);
		MainFrame.btnSearchSimilarFiles.setEnabled(true);
	}
	
	

}
