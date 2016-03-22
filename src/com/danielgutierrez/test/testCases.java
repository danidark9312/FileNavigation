package com.danielgutierrez.test;

import java.io.IOException;

import org.junit.Test;

import com.danielgutierrez.thread.OperationManager;

public class testCases {
	
//	@Test
	public void testFileLookUp(){
		//new OperationManager().main(null);
	}
	
//	@Test
	public void testReadFromFile(){
		try {
			OperationManager.getInstance().readFilesIntoList(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetCandidates(){
		try {
			
			OperationManager.getInstance().readFilesIntoList(null);
			OperationManager.getInstance().extractCandidatesFiles();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
