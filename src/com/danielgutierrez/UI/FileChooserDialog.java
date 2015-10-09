package com.danielgutierrez.UI;

import java.awt.EventQueue;
import java.awt.FileDialog;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class FileChooserDialog extends JPanel{
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileChooserDialog window = new FileChooserDialog();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FileChooserDialog() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setBounds(100, 100, 552, 329);
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new GridLayout(2, 1, 0, 0));
		
		JLabel lblPleaseDragAnd = new JLabel("Please drag and drop the files into the table, folders will be ignored");
		lblPleaseDragAnd.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblPleaseDragAnd);
		
		JLabel lblpressRightClick = new JLabel("(press Right click to remove a record)");
		lblpressRightClick.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblpressRightClick);
		
		JPanel panel = new JPanel();
		
		
		add(panel, BorderLayout.CENTER);
		
		
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);
		
		//JPanel tableContainer = new JPanel();
		//tableContainer.setLayout(new BorderLayout(0, 0));
		
		table = new JTable();
		scrollPane.add(table);
		//panel.add(table, BorderLayout.NORTH);
		//panel.add(table, BorderLayout.SOUTH);
		table.setModel(new DefaultTableModel(
				new String[]{new String[]{"file"},
			new String[] {
				"Archivo"
			}
		));
		
		table.setDragEnabled(true);
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setTransferHandler(getTransderForFile(table.getModel()));
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent event){
				if (event.getButton() == MouseEvent.BUTTON3){
					((DefaultTableModel)table.getModel()).removeRow(table.rowAtPoint(event.getPoint()));
				}
			}
		});
		
		JButton btnSelectFiles = new JButton("Select Files");
		add(btnSelectFiles, BorderLayout.SOUTH);
	}
	
	private TransferHandler getTransderForFile(TableModel tableModel){
		return new TransferHandler(){
			@Override
	        public boolean canImport(TransferHandler.TransferSupport info) {
	            if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	                return false;
	            }
	            return true;
	        }
			 @Override
		        public boolean importData(TransferHandler.TransferSupport info) {
		            if (!info.isDrop()) {
		                return false;
		            }

		            if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
		                displayDropLocation("List doesn't accept a drop of this type.");
		                return false;
		            }

		            Transferable t = info.getTransferable();
		            List<File> data;
		            int row = ((JTable.DropLocation)info.getDropLocation()).getRow();
		            try {
		                data = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
		            } 
		            catch (Exception e) { return false; }
		            DefaultTableModel model = (DefaultTableModel)tableModel; 
		            for (File file : data) {
		            	if(!file.isDirectory() && !fileAlreadyAddes(model,file))
		            		model.insertRow(row,new Object[]{file.getAbsolutePath(),""});
		            }
		            return true;
		        }

		        private void displayDropLocation(String string) {
		            System.out.println(string);
		        }
		        private boolean fileAlreadyAddes(DefaultTableModel model, File file){
		        	for(Object fileAdded : model.getDataVector()){
		        		if(((Vector)fileAdded).get(0).equals(file.getAbsolutePath())){
		        			return true;
		        		}
		        	}
		        	return false;
		        }
		    };
			
		}
	
}
