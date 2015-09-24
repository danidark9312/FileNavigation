package com.danielgutierrez.test;

import java.io.IOException;

import org.junit.Test;

import com.danielgutierrez.FilesLookUp.Main;

public class testCases {
	
//	@Test
	public void testFileLookUp(){
		new Main().main(null);
	}
	
//	@Test
	public void testReadFromFile(){
		try {
			new Main().readFilesIntoList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetCandidates(){
		try {
			Main main = new Main();
			main.readFilesIntoList();
			main.extractCandidatesFiles();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
