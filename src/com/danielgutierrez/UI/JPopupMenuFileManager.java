package com.danielgutierrez.UI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;


public class JPopupMenuFileManager extends JPopupMenu{

	private JTable contentTable;
	private int x,y;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable(){

			public void run() {
				try {
					JPopupMenuFileManager frame = new JPopupMenuFileManager();
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
	private JPopupMenuFileManager(){
		this(100,100,null);
	}
	/**
	 * Create the frame.
	 */
	public JPopupMenuFileManager(int x,int y,JTable jtable){
		this.x = x;
		this.y = y;
		this.contentTable = jtable;
		
		
		//setBounds(x, y, 450, 300);
		JMenuItem jMenuItem = new JMenuItem("copy absoluth path to clipboard");
		
		jMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = contentTable.rowAtPoint(new Point(x, y));
				contentTable.setRowSelectionInterval(selectedRow, selectedRow);
				String path = (String) contentTable.getModel().getValueAt(selectedRow, 1);
				copyContentToClip(path);
			}
		});
		
		add(jMenuItem);
		
	}
	
	
	public void show(){
		this.show(contentTable, x, y);
	}
	
	
	
	private void copyContentToClip(String content){
		StringSelection stringSelection = new StringSelection(content);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}
}
