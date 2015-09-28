package com.danielgutierrez.UI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.RepaintManager;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.danielgutierrez.filesLookUp.OperationManager;
import com.danielgutierrez.workers.LogWorker;
import com.danielgutierrez.workers.ManagerWorker;

import java.awt.FileDialog;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class MainFrame {
	private JLabel lblBaseFolder;
	private JFrame frame;
	private OperationManager manager;
	private File baseDir;
	private JTextPane txtPane; 

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

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		manager = OperationManager.getInstance();
		
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setValue(50);
		frame.getContentPane().add(progressBar, BorderLayout.SOUTH);
		
		JPanel pnlButtonsTool = new JPanel();
		pnlButtonsTool.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		frame.getContentPane().add(pnlButtonsTool, BorderLayout.WEST);
		pnlButtonsTool.setLayout(new GridLayout(10, 1, 0, 0));
		
		JButton btnScanDisk = new JButton("Scan Disk");
		btnScanDisk.addActionListener(new ActionListener() {			public void actionPerformed(ActionEvent e) {
				manager.setParameterScan(baseDir.getAbsolutePath(), false);
				LogWorker.turnonLogFlag();
				new LogWorker(manager).execute();
				new ManagerWorker(manager, ManagerWorker.OPERATION_SCAN).execute();
			}
		});
		
		
		JButton btnSelectBaseFolder = new JButton("Select Base Folder");
		btnSelectBaseFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(frame);
				 if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
			            //This is where a real application would open the file.
			            lblBaseFolder.setText(file.getAbsolutePath());
			            baseDir = file;
			            frame.repaint();
			        } else {
			            JOptionPane optionPane = new JOptionPane("canceled file searcher");
			            optionPane.show();
			        }
			}
		});
		pnlButtonsTool.add(btnSelectBaseFolder);
		pnlButtonsTool.add(btnScanDisk);
		
		JButton btnSaveResult = new JButton("Save Scan");
		btnSaveResult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.SAVE);
				fd.setFile("*.dat");
				fd.setVisible(true);
				if(fd.getFile()!=null)
					try {
						OperationManager.getInstance().writeFilesIntoFile(new File(fd.getFile()));
						JOptionPane.showMessageDialog(frame, "Se escribió con éxito");
					} catch (IOException e) {
						e.printStackTrace();
					}
				else
					JOptionPane.showMessageDialog(frame, "Se cancelo la seleccion de archivo");
			}
		});
		pnlButtonsTool.add(btnSaveResult);
		
		JButton btnLoadScan = new JButton("Load Scan");
		btnLoadScan.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
						fd.setFile("*.dat");
			            fd.setVisible(true);
			            String file = fd.getFile();
			            if(file!=null){
			            try {
			            	System.out.println("file: "+file);
							manager.readFilesIntoList(new File(file));
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(frame, "No se encuentra el archivo");
						}
			        } else {
			        	JOptionPane.showMessageDialog(frame, "Se cancelo la seleccion");
			            
			        }
			}
		
		});
		pnlButtonsTool.add(btnLoadScan);
		
		JButton btnSearchSimilarFiles = new JButton("Search Similar");
		btnSearchSimilarFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				manager.getInstance().extractCandidatesFiles();
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

}
