package com.danielgutierrez.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.danielgutierrez.filesLookUp.FileCached;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JSplitPane;
import javax.swing.JSeparator;


public class JTableResultScreen extends JFrame{

	private JPanel contentPane;
	private JTable table;
	private List<List<FileCached>> candidateGroup;
	private Object[][] dataSelected;
	private int countDeleteFiles;
	private JLabel lblSelectedFiles;
	private JLabel lblDeletedFiles;

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
		this.candidateGroup = candidateGroup;
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
		JpnlActionPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		JpnlActionPane.add(panel, BorderLayout.WEST);
		
		JButton btnNewButton = new JButton("delete");
		panel.add(btnNewButton);
		
		panel.add(new JLabel("Selected/Deleted Files: "));
		lblSelectedFiles = new JLabel("0");
		panel.add(lblSelectedFiles);
		lblSelectedFiles.setHorizontalAlignment(SwingConstants.LEFT);
		
		
		panel.add(new JLabel("/"));
		
		lblDeletedFiles = new JLabel("0");
		panel.add(lblDeletedFiles);
		
		JSeparator separator = new JSeparator();
		JpnlActionPane.add(separator, BorderLayout.NORTH);
		separator.setOrientation(SwingConstants.VERTICAL);
		
		JLabel lblFooter = new JLabel("Animaccion3D/danidark9312");
		lblFooter.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
		JpnlActionPane.add(lblFooter, BorderLayout.CENTER);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				deleteSelectedFiles();
			}
		});
		
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
						
						if (row!=-1) {
							System.out.println("column selected: " + row);
							TableModel model = table.getModel();
							Boolean isChecked = (Boolean) model.getValueAt(row, 3);
							System.out.println("column checked " + isChecked);
							if (isChecked)
								model.setValueAt(Boolean.FALSE, row, 3);
							else
								model.setValueAt(Boolean.TRUE, row, 3);
						}
						countSelectedFile();
					}
				}
			});;
			
			JTableContent.add(table.getTableHeader(),BorderLayout.NORTH);
			JTableContent.add(table,BorderLayout.CENTER);
		contentPane.add(scrllPane, BorderLayout.CENTER);
	}
	
	
	private void countSelectedFile() {
		dataSelected = ((JTableModelFileManager) table.getModel()).getDataChecked();
		setLabelValue(lblSelectedFiles, dataSelected.length);
	}
	
	private void setLabelValue(JLabel jLabel, Object text){
		dataSelected =  ((JTableModelFileManager)table.getModel()).getDataChecked();
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				jLabel.setText(String.valueOf(text));
			}
		});
	}
	
	
	
	
	private void deleteSelectedFiles(){
		if(dataSelected == null){
			return;
		}
		int totalSizeDeleted = 0;
		//Object[][] dataSelected =  ((JTableModelFileManager)table.getModel()).getDataChecked();
		int selectedFiles = dataSelected.length;
		
		int confirmationDelete = MainFrame.showConfirmDialog(this, "Are you sure you want to delete permanently this "+selectedFiles+" files ?", "Warning");
		
		if (confirmationDelete == JOptionPane.YES_OPTION) {
			File temp;
			MainFrame.showDialog("Deleting selected files...",false);
			for (Object[] object : dataSelected) {
				temp = new File((String) object[1]);
				long tempSize = temp.length();
				if(temp.delete()){
					totalSizeDeleted += tempSize;
					removeElementFromList(temp);
					setLabelValue(lblDeletedFiles, ++countDeleteFiles);
				}
			}
			MainFrame.hideDialog();
			
			((JTableModelFileManager) table.getModel()).reloadDataTable();
			JOptionPane.showMessageDialog(this, "The files has been deleted successfull, now you have "+FileCached.readableFileSize(totalSizeDeleted)+" more on free space");
			setLabelValue(lblDeletedFiles, 0);
		}
	}
	private void removeElementFromList(File temp){
		FileCached fileCached;
		for(List<FileCached> fileLists : candidateGroup){
			for(int i = 0;i<fileLists.size();i++){
				fileCached = fileLists.get(i);
				if(fileCached.getFile().equals(temp)){
					fileLists.remove(i);
					i--;
				}
			}
		}
	}
}
