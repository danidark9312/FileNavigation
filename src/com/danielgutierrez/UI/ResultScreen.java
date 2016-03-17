package com.danielgutierrez.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;


public class ResultScreen extends JFrame{

	private JPanel contentPane;
	private JTextPane txtPaneResult;
	private String result;
	private JScrollPane scrollPane;
	
	
	/**
	 * Launch the application.
	 */
	public static void showResult(String result) {
		
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				try {
					ResultScreen frame = new ResultScreen(result);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] result) {
		
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				try {
					ResultScreen frame = new ResultScreen("test");
					
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private ResultScreen(String info){
		this.result = info;
		init();
	}
	
	private void init(){
		
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(1, 1, 1, 1));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		scrollPane = new JScrollPane();
		
		txtPaneResult = new JTextPane();
		txtPaneResult.setEditable(false);
		//contentPane.add(textPane, BorderLayout.CENTER);
		
		scrollPane.setViewportView(txtPaneResult);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0.5f,0.5f,0.5f,0.2f));
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblFilesResult = new JLabel("Files Result");
		lblFilesResult.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblFilesResult, BorderLayout.CENTER);
	}
}
