package com.danielgutierrez.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.danielgutierrez.filesLookUp.FileCached;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class JTableResultScreen extends JFrame{

	private JPanel contentPane;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable(){

			public void run() {
				try {
					JTableResultScreen frame = new JTableResultScreen(null);
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
	public JTableResultScreen(List<List<FileCached>> candidateGroup){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBounds(100, 100, 661, 441);
		contentPane = new JPanel();
		JPanel JTableContent = new JPanel(new BorderLayout(0,0));
		
		
		JScrollPane scrllPane = new JScrollPane();
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel JpnlActionPane = new JPanel();
		
		scrllPane.setViewportView(JTableContent);
		contentPane.add(JpnlActionPane, BorderLayout.NORTH);
		
		JButton btnNewButton = new JButton("delete");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				deleteSelectedFiles();
			}
		});
		JpnlActionPane.add(btnNewButton);
		
		if(candidateGroup!=null)
			table = new JTable(new JTableModelFileManager(candidateGroup));
		else
			table = new JTable();
			
			table.getColumnModel().getColumn(0).setPreferredWidth(100);
			table.getColumnModel().getColumn(0).setMinWidth(100);
			table.getColumnModel().getColumn(0).setMaxWidth(300);
			
			table.getColumnModel().getColumn(1).setPreferredWidth(600);
			table.getColumnModel().getColumn(1).setMinWidth(300);
			table.getColumnModel().getColumn(1).setMaxWidth(800);
			
			table.getColumnModel().getColumn(2).setPreferredWidth(40);
			table.getColumnModel().getColumn(2).setMinWidth(40);
			table.getColumnModel().getColumn(2).setMaxWidth(80);
			
			table.getColumnModel().getColumn(3).setPreferredWidth(10);
			table.getColumnModel().getColumn(3).setMinWidth(10);
			table.getColumnModel().getColumn(3).setMaxWidth(50);
			
			table.setPreferredScrollableViewportSize(new Dimension(1200, 70));
			
			
			table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					 
					if (e.getValueIsAdjusting()) {
						System.out.println("selection done");
						int row = table.getSelectedRow();
						System.out.println("column selected: " + row);
						TableModel model = table.getModel();
						Boolean isChecked = (Boolean) model.getValueAt(row, 3);
						System.out.println("column checked " + isChecked);
						if (isChecked)
							model.setValueAt(Boolean.FALSE, row, 3);
						else
							model.setValueAt(Boolean.TRUE, row, 3);
					}
					
				}
			});;
			
		JTableContent.add(table.getTableHeader(),BorderLayout.NORTH);
		JTableContent.add(table,BorderLayout.CENTER);
		contentPane.add(scrllPane, BorderLayout.CENTER);
	}
	
	private void deleteSelectedFiles(){
		Object[][] dataSelected =  ((JTableModelFileManager)table.getModel()).getDataChecked();
		for(Object[] object : dataSelected){
			System.out.println(object[0]);
		}
		
	}
	
}
