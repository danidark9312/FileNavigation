package com.danielgutierrez.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import com.danielgutierrez.filesLookUp.OperationManager;
import com.danielgutierrez.workers.LogWorker;
import com.danielgutierrez.workers.ManagerWorker;

public class MainFrame {
	private JLabel lblBaseFolder;
	private JFrame frame;
	private OperationManager manager;
	private File baseDir;
	private JTextPane txtPane;
	private JButton btnScanDisk;
	public static JButton btnSaveResult;
	public static JButton btnSearchSimilarFiles;
	private static ModalMessage modalMessage;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
	}

	private void selectBaseDirectoryAction(){
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(frame);
		 if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            btnScanDisk.setEnabled(true);
	            //This is where a real application would open the file.
	            lblBaseFolder.setText(file.getAbsolutePath());
	            baseDir = file;
	        }
	}
	
	private void scanAction(){
			manager.setParameterScan(baseDir.getAbsolutePath(), false);
			LogWorker.turnonLogFlag();
			new LogWorker(manager).execute();
			new ManagerWorker(manager, ManagerWorker.OPERATION_SCAN).execute();
	}
	
	private void saveResultAction(){
		FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.SAVE);
		fd.setFile("scan.res");
		fd.setVisible(true);
		if(fd.getFile()!=null)
			try {
				OperationManager.getInstance().writeFilesIntoFile(new File(fd.getFile()));
				JOptionPane.showMessageDialog(frame, "Saved Successfull");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	
	private void compareSimilarAction(){
		
		FileDialog fd = new FileDialog(frame, "Save Result", FileDialog.SAVE);
		fd.setFile("scan.dup");
		fd.setVisible(true);
		if(fd.getFile()!=null){
			manager.setParameterCompare(new File(fd.getFile()));
			new LogWorker(manager).execute();
			new ManagerWorker(manager, ManagerWorker.OPERATION_COMPARE).execute();
			
		}
	}
	
	private void loadScanAction(){
		
		FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
				fd.setFile("scan.res");
	            fd.setVisible(true);
	            String file = fd.getFile();
	            if(file!=null){
	            try {
					manager.readFilesIntoList(new File(file));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame, "No se encuentra el archivo");
				}
	        }
	
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	
	private void initialize() {
		
		manager = OperationManager.getInstance();
		modalMessage = new ModalMessage(frame, "");
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setForeground(Color.blue);
		//progressBar.setForeground(new Color(246, 156, 85));
		progressBar.setValue(50);
		progressBar.setStringPainted(true);
		
		frame.getContentPane().add(progressBar, BorderLayout.SOUTH);
		
		JPanel pnlButtonsTool = new JPanel();
		pnlButtonsTool.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		
		frame.getContentPane().add(pnlButtonsTool, BorderLayout.WEST);
		pnlButtonsTool.setLayout(new GridLayout(10, 1, 0, 0));
		
		btnScanDisk = new JButton("Scan Disk");
		btnScanDisk.setEnabled(false);
		btnScanDisk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scanAction();
			}
		});
		JButton btnSelectBaseFolder = new JButton("Select Base Folder");
		btnSelectBaseFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectBaseDirectoryAction();
			}
		});
		pnlButtonsTool.add(btnSelectBaseFolder);
		pnlButtonsTool.add(btnScanDisk);
		
		btnSaveResult = new JButton("Save Scan");
		btnSaveResult.setEnabled(false);
		btnSaveResult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			saveResultAction();
			}
		});
		pnlButtonsTool.add(btnSaveResult);
		
		JButton btnLoadScan = new JButton("Load Scan");
		btnLoadScan.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				loadScanAction();
			}
		
		});
		pnlButtonsTool.add(btnLoadScan);
		
		btnSearchSimilarFiles = new JButton("Search Similar");
		btnSearchSimilarFiles.setEnabled(false);
		btnSearchSimilarFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				compareSimilarAction();
			}
		});
		pnlButtonsTool.add(btnSearchSimilarFiles);
		
		JButton btnClearConsole = new JButton("Clear Console");
		btnClearConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtPane.setText("");
			}
		});
		pnlButtonsTool.add(btnClearConsole);
		
		JLabel lblSimfileV = new JLabel("SimFile V1.0");
		frame.getContentPane().add(lblSimfileV, BorderLayout.NORTH);
		
		JPanel pnlScrollContainer = new JPanel();
		frame.getContentPane().add(pnlScrollContainer, BorderLayout.CENTER);
		pnlScrollContainer.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		pnlScrollContainer.add(scrollPane);
		
		txtPane = new JTextPane();
		txtPane.setEditable(false);
		scrollPane.setViewportView(txtPane);
		
		JPanel panel = new JPanel();
		pnlScrollContainer.add(panel, BorderLayout.NORTH);
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{149, 149, 0};
		gbl_panel.rowHeights = new int[]{16, 16, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		//lblBaseFolder = new JLabel("Folder Selected:");
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel label = new JLabel("Folder Selected:");
		panel.add(label, gbc);
		
		lblBaseFolder = new JLabel("none");
		GridBagConstraints gbc_lblBaseFolder = new GridBagConstraints();
		gbc_lblBaseFolder.gridwidth = 2;
		gbc_lblBaseFolder.anchor = GridBagConstraints.WEST;
		gbc_lblBaseFolder.fill = GridBagConstraints.VERTICAL;
		gbc_lblBaseFolder.insets = new Insets(0, 0, 5, 0);
		gbc_lblBaseFolder.gridx = 1;
		gbc_lblBaseFolder.gridy = 0;
		panel.add(lblBaseFolder, gbc_lblBaseFolder);
		
		JLabel lblFilesFound = new JLabel("Files Found:");
		GridBagConstraints gbc_lblFilesFound = new GridBagConstraints();
		gbc_lblFilesFound.fill = GridBagConstraints.BOTH;
		gbc_lblFilesFound.insets = new Insets(0, 0, 0, 5);
		gbc_lblFilesFound.gridx = 0;
		gbc_lblFilesFound.gridy = 1;
		panel.add(lblFilesFound, gbc_lblFilesFound);
		
		JLabel lblFiles = new JLabel("0");
		GridBagConstraints gbc_lblFiles = new GridBagConstraints();
		gbc_lblFiles.fill = GridBagConstraints.BOTH;
		gbc_lblFiles.gridx = 1;
		gbc_lblFiles.gridy = 1;
		panel.add(lblFiles, gbc_lblFiles);
		
		
		manager.initDialog(progressBar,txtPane);
	}
	
	public static void showDialog(String text){
		modalMessage.showDialog(text);
	}
	public static void hideDialog(){
		modalMessage.hideDialog();
	}

}
