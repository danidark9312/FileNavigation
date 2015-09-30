package com.danielgutierrez.UI;

import java.awt.EventQueue;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import java.awt.Font;

public class ModalMessage extends JDialog{
	JLabel lblInfo;
	

	/**
	 * Create the application.
	 */
	public ModalMessage(Frame frame,String text) {
		super(frame,"Info",true);
		lblInfo = new JLabel("Info");
		lblInfo.setText(text);		
		initialize();
	}
	
	public void setInfo(String text){
		lblInfo.setText(text);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//frame = new JFrame();
		setBounds(100, 100, 295, 185);
		
		
		
		lblInfo.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblInfo, BorderLayout.CENTER);
	}
	
	
	
	public void showDialog(String text){
		this.lblInfo.setText(text);
		setVisible(true);
	}
	
	
	public void hideDialog(){
		setVisible(false);
	}
}
