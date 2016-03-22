
package com.danielgutierrez.UI;


import java.awt.BorderLayout;
import java.awt.Component;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;



import com.danielgutierrez.thread.FileCached;
import com.danielgutierrez.thread.OperationManager;
import com.danielgutierrez.workers.LogWorker;
import com.danielgutierrez.workers.ManagerWorker;

import java.awt.FlowLayout;

public class MainFrame{

	private JPanel filePanel;
	private static final String version = "SimFile V1.0 Alpha";
	public static JButton btnSaveResult;
	public static JButton btnSearchSimilarFiles;
	private static ModalMessage modalMessage;
	public static JLabel lblFiles;
	private static FileChooserDialog chooserDialog;

	public static void hideDialog() {
		modalMessage.hideDialog();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable(){

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

	public static int showConfirmDialog(Component component, String message, String tittle) {
		int res = JOptionPane.showOptionDialog(component, message, tittle, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] {
				"Yes", "No" }, // this is the array
				"default");
		return res;
	}

	public static void showDialog(String text) {
		modalMessage.showDialog(text,false);
	}
	public static void showDialog(String text,boolean closeable) {
		modalMessage.showDialog(text,closeable);
	}

	private JLabel lblBaseFolder;
	private JFrame frame;
	private OperationManager manager;
	private File baseDir;
	private JTextPane txtPane;
	private JButton btnScanDisk;

	/**
	 * Create the application.
	 */
	public MainFrame(){
		initialize();
	}

	/**
	 * Accion donde se comparan todos los archivos escaneados en busca de repetidos
	 */
	private void compareSimilarAction() {
		FileDialog fd = new FileDialog(frame, "Save Result", FileDialog.SAVE);
		fd.setFile("scan.dup");
		fd.setVisible(true);
		if (fd.getFile() != null) {
			FileCached filesSelected[] = chooserDialog.getAllFilesAdded();
			manager.setParameterCompare(new File(fd.getFile()), filesSelected);
			LogWorker.turnonLogFlag();
			new LogWorker(manager).execute();
			new ManagerWorker(manager, ManagerWorker.OPERATION_COMPARE).execute();
		}
	}

	private void initialize() {
		manager = OperationManager.getInstance();
		modalMessage = new ModalMessage(frame, "");
		frame = new JFrame();
		frame.setBounds(100, 100, 558, 394);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		JPanel configPanel = new JPanel();
		frame.getContentPane().add(configPanel);
		configPanel.setLayout(new BorderLayout(0, 0));
		JPanel pnlButtonsTool = new JPanel();
		configPanel.add(pnlButtonsTool, BorderLayout.WEST);
		pnlButtonsTool.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagLayout gbl_pnlButtonsTool = new GridBagLayout();
		gbl_pnlButtonsTool.columnWidths = new int[] { 121, 0 };
		gbl_pnlButtonsTool.rowHeights = new int[] { 17, 17, 17, 17, 17, 0, 0 };
		gbl_pnlButtonsTool.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_pnlButtonsTool.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		pnlButtonsTool.setLayout(gbl_pnlButtonsTool);
		btnSearchSimilarFiles = new JButton("Search Similar");
		btnSearchSimilarFiles.setEnabled(false);
		btnSearchSimilarFiles.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				compareSimilarAction();
			}
		});
		btnSaveResult = new JButton("Save Scan");
		btnSaveResult.setEnabled(false);
		btnSaveResult.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				saveResultAction();
			}
		});
		JButton btnSelectBaseFolder = new JButton("Select Base Folder");
		btnSelectBaseFolder.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				selectBaseDirectoryAction();
			}
		});
		GridBagConstraints gbc_btnSelectBaseFolder = new GridBagConstraints();
		gbc_btnSelectBaseFolder.fill = GridBagConstraints.BOTH;
		gbc_btnSelectBaseFolder.insets = new Insets(0, 0, 5, 0);
		gbc_btnSelectBaseFolder.gridx = 0;
		gbc_btnSelectBaseFolder.gridy = 0;
		pnlButtonsTool.add(btnSelectBaseFolder, gbc_btnSelectBaseFolder);
		btnScanDisk = new JButton("Scan Disk");
		btnScanDisk.setEnabled(false);
		
		btnScanDisk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scanAction();
			}
		});
		GridBagConstraints gbc_btnScanDisk = new GridBagConstraints();
		gbc_btnScanDisk.fill = GridBagConstraints.BOTH;
		gbc_btnScanDisk.insets = new Insets(0, 0, 5, 0);
		gbc_btnScanDisk.gridx = 0;
		gbc_btnScanDisk.gridy = 1;
		pnlButtonsTool.add(btnScanDisk, gbc_btnScanDisk);
		GridBagConstraints gbc_btnSaveResult = new GridBagConstraints();
		gbc_btnSaveResult.fill = GridBagConstraints.BOTH;
		gbc_btnSaveResult.insets = new Insets(0, 0, 5, 0);
		gbc_btnSaveResult.gridx = 0;
		gbc_btnSaveResult.gridy = 2;
		pnlButtonsTool.add(btnSaveResult, gbc_btnSaveResult);
		JButton btnLoadScan = new JButton("Load Scan");
		btnLoadScan.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				loadScanAction();
			}
		});
		GridBagConstraints gbc_btnLoadScan = new GridBagConstraints();
		gbc_btnLoadScan.fill = GridBagConstraints.BOTH;
		gbc_btnLoadScan.insets = new Insets(0, 0, 5, 0);
		gbc_btnLoadScan.gridx = 0;
		gbc_btnLoadScan.gridy = 3;
		pnlButtonsTool.add(btnLoadScan, gbc_btnLoadScan);
		GridBagConstraints gbc_btnSearchSimilarFiles = new GridBagConstraints();
		gbc_btnSearchSimilarFiles.insets = new Insets(0, 0, 5, 0);
		gbc_btnSearchSimilarFiles.fill = GridBagConstraints.BOTH;
		gbc_btnSearchSimilarFiles.gridx = 0;
		gbc_btnSearchSimilarFiles.gridy = 4;
		pnlButtonsTool.add(btnSearchSimilarFiles, gbc_btnSearchSimilarFiles);
		JCheckBox chckbxFindAll = new JCheckBox("Find all files");
		chckbxFindAll.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				validateChangeCheckBox(e);
			}
		});
		GridBagConstraints gbc_chckbxFindAllFiles = new GridBagConstraints();
		gbc_chckbxFindAllFiles.gridx = 0;
		gbc_chckbxFindAllFiles.gridy = 5;
		pnlButtonsTool.add(chckbxFindAll, gbc_chckbxFindAllFiles);
		JLabel lblSimfileV = new JLabel(version);
		configPanel.add(lblSimfileV);
		JPanel pnlScrollContainer = new JPanel();
		configPanel.add(pnlScrollContainer);
		pnlScrollContainer.setLayout(new BorderLayout(0, 0));
		JScrollPane scrollPane = new JScrollPane();
		pnlScrollContainer.add(scrollPane);
		txtPane = new JTextPane();
		txtPane.setEditable(false);
		scrollPane.setViewportView(txtPane);
		JPanel pnlInfo = new JPanel();
		pnlScrollContainer.add(pnlInfo, BorderLayout.NORTH);
		pnlInfo.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagLayout gbl_pnlInfo = new GridBagLayout();
		gbl_pnlInfo.columnWidths = new int[] { 100, 100, 149 };
		gbl_pnlInfo.rowHeights = new int[] { 30, 16 };
		gbl_pnlInfo.columnWeights = new double[] { 0.0, 0.0, 1.0 };
		gbl_pnlInfo.rowWeights = new double[] { 0.0, 1.0 };
		pnlInfo.setLayout(gbl_pnlInfo);
		// lblBaseFolder = new JLabel("Folder Selected:");
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.5;
		gbc.gridwidth = 0;
		gbc.insets = new Insets(0, 0, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel label = new JLabel("Folder Selected:");
		pnlInfo.add(label, gbc);
		lblBaseFolder = new JLabel("none");
		GridBagConstraints gbc_lblBaseFolder = new GridBagConstraints();
		gbc_lblBaseFolder.weightx = 0.5;
		gbc_lblBaseFolder.gridwidth = 0;
		gbc_lblBaseFolder.anchor = GridBagConstraints.WEST;
		gbc_lblBaseFolder.fill = GridBagConstraints.VERTICAL;
		gbc_lblBaseFolder.insets = new Insets(0, 0, 5, 5);
		gbc_lblBaseFolder.gridx = 1;
		gbc_lblBaseFolder.gridy = 0;
		pnlInfo.add(lblBaseFolder, gbc_lblBaseFolder);
		JPanel panelCleanBtn = new JPanel();
		GridBagConstraints gbc_panelCleanBtn = new GridBagConstraints();
		gbc_panelCleanBtn.anchor = GridBagConstraints.EAST;
		gbc_panelCleanBtn.gridwidth = 0;
		gbc_panelCleanBtn.insets = new Insets(0, 0, 5, 0);
		gbc_panelCleanBtn.gridheight = 2;
		gbc_panelCleanBtn.gridx = 2;
		gbc_panelCleanBtn.gridy = 0;
		pnlInfo.add(panelCleanBtn, gbc_panelCleanBtn);
		panelCleanBtn.setLayout(new BorderLayout(5, 5));
		JButton btnClean = new JButton("");
		btnClean.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				txtPane.setText("");
			}
		});
		btnClean.setIcon(new ImageIcon("D:\\DanielGutierrez\\Proyectos\\FileNavigation\\FileNavigation\\icons\\clearConsole2.png"));
		panelCleanBtn.add(btnClean, BorderLayout.CENTER);
		JLabel lblFilesFound = new JLabel("Files Found:");
		GridBagConstraints gbc_lblFilesFound = new GridBagConstraints();
		gbc_lblFilesFound.weightx = 0.5;
		gbc_lblFilesFound.anchor = GridBagConstraints.WEST;
		gbc_lblFilesFound.gridwidth = 0;
		gbc_lblFilesFound.fill = GridBagConstraints.BOTH;
		gbc_lblFilesFound.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilesFound.gridx = 0;
		gbc_lblFilesFound.gridy = 1;
		pnlInfo.add(lblFilesFound, gbc_lblFilesFound);
		lblFiles = new JLabel("0");
		GridBagConstraints gbc_lblFiles = new GridBagConstraints();
		gbc_lblFiles.weightx = 0.5;
		gbc_lblFiles.anchor = GridBagConstraints.WEST;
		gbc_lblFiles.gridwidth = 0;
		gbc_lblFiles.insets = new Insets(0, 0, 5, 5);
		gbc_lblFiles.fill = GridBagConstraints.BOTH;
		gbc_lblFiles.gridx = 1;
		gbc_lblFiles.gridy = 1;
		pnlInfo.add(lblFiles, gbc_lblFiles);
		filePanel = new JPanel();
		frame.getContentPane().add(filePanel);
		filePanel.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlAuthorInfo = new JPanel();
		filePanel.add(pnlAuthorInfo, BorderLayout.SOUTH);
		FlowLayout fl_pnlAuthorInfo = (FlowLayout) pnlAuthorInfo.getLayout();
		fl_pnlAuthorInfo.setHgap(10);
		
		JLabel lblCopyrightDanidarkanimacciondAll = new JLabel("Copyright danidark9312/Animaccion3D all rights reserved");
		pnlAuthorInfo.add(lblCopyrightDanidarkanimacciondAll);
		JProgressBar progressBar = new JProgressBar();
		filePanel.add(progressBar, BorderLayout.NORTH);
		// progressBar.setForeground(Color.blue);
		// progressBar.setForeground(new Color(246, 156, 85));
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		manager.initDialog(progressBar, txtPane);
		chooserDialog = new FileChooserDialog();
		filePanel.add(chooserDialog);
		chooserDialog.setVisible(true);
	}

	/**
	 * Metodo para cargar un listado de archivos previamente escaneados
	 */
	private void loadScanAction() {
		FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
		fd.setFile("scan.res");
		fd.setVisible(true);
		String file = fd.getFile();
		if (file != null) {
			try {
				manager.readFilesIntoList(new File(file));
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, "No se encuentra el archivo");
			}
		}
	}

	/**
	 * Almacenamos los resultados del proceso para ser mostrados
	 */
	private void saveResultAction() {
		FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.SAVE);
		fd.setFile("scan.res");
		fd.setVisible(true);
		if (fd.getFile() != null)
			try {
				OperationManager.getInstance().writeFilesIntoFile(new File(fd.getFile()));
				JOptionPane.showMessageDialog(frame, "Saved Successfull");
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Se inicia el escaneo en la carpeta seleccionada
	 */
	private void scanAction() {
		FileCached filesSelected[] = chooserDialog.getAllFilesAdded();
		
		if ((!filePanel.equals(chooserDialog.getParent())&& filesSelected == null)
				||(filePanel.equals(chooserDialog.getParent()) && filesSelected != null)){
			manager.setParameterScan(baseDir.getAbsolutePath(), false);
			LogWorker.turnonLogFlag();
			new LogWorker(manager).execute();
			new ManagerWorker(manager, ManagerWorker.OPERATION_SCAN).execute();
		} else {
			
			showDialog("You must add at least one file to be scanned or mark the option \"Find al files\"");
		}
	}

	/**
	 * Se selecciona la carpeta raíz base con un componente selector de archivo
	 */
	private void selectBaseDirectoryAction() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			btnScanDisk.setEnabled(true);
			// This is where a real application would open the file.
			lblBaseFolder.setText(file.getAbsolutePath());
			baseDir = file;
		}
	}

	
	private void validateChangeCheckBox(ActionEvent changeEvent) {
		JCheckBox box = (JCheckBox) changeEvent.getSource();
		if (box.isSelected()) {
			if ((showConfirmDialog(frame,
					"Matching all file may take too much time, you should select a base file directory carefully, do you want to continue ?", "Warning!") != JOptionPane.YES_OPTION)) {
				((JCheckBox) changeEvent.getSource()).setSelected(false);
				filePanel.add(chooserDialog);
				//chooserDialog.setVisible(true);
			} else {
				//chooserDialog.setVisible(false);
				filePanel.remove(chooserDialog);
				chooserDialog.cleanAllElementsInTable();
			}
		} else {
			filePanel.add(chooserDialog);
			//chooserDialog.setVisible(true);
		}
		
		frame.repaint();
	}
	
}

