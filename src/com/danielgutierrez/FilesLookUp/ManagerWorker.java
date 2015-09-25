package com.danielgutierrez.filesLookUp;

import java.util.List;

import javax.swing.SwingWorker;

public class ManagerWorker<T, V> extends SwingWorker<Object,Object> {
	OperationManager manager; 
	private boolean flagWriteLog = true; 
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
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(flagWriteLog){
						publish(new Object());
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {}	
					}
					
				}
			}).start();
			/*tr.setPriority(Thread.MAX_PRIORITY);
			tr.start();*/
			manager.scanNow(manager.getBaseDir(), false,this);
			this.flagWriteLog = false;
			//tr.interrupt();
			break;
		case OPERATION_COMPARE:

			break;

		default:
			break;
		}
		
		return null;
				
	}
	
	
	
	@Override
	protected void process(List<Object> v){
		manager.dumpToLogStack();		
	}

}
