package com.bibler.awesome.bibnesburner.ui;

import java.io.FileInputStream;

import javax.swing.JFrame;

import com.bibler.awesome.bibnesburner.burnerutils.BitBurner;
import com.bibler.awesome.bibnesburner.fileutils.FileLoader;
import com.bibler.awesome.bibnesburner.fileutils.NESFile;
import com.bibler.awesome.bibnesburner.serialutils.SerialPortManager;

public class MainFrame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6034318322323330089L;
	private MainPanel panel;
	private BitBurner burner;
	private FileLoader loader;
	private SerialPortManager serialManager;
	
	public MainFrame() {
		super();
		serialManager = new SerialPortManager();
		burner = new BitBurner();
		loader = new FileLoader();
		panel = new MainPanel(this);
		loader.registerObjectToNotify(panel.getInfoPanel());
		burner.registerObjectToNotify(panel.getInfoPanel());
		burner.registerObjectToNotify(panel.getMessagePanel());
		burner.registerObjectToNotify(panel.getHexPanel());
		add(panel);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void loadNewFile() {
		FileInputStream f = loader.loadFile();
		NESFile nesFile = new NESFile(f, panel.getInfoPanel(), loader.getBin());
		panel.getHexPanel().setHexData(nesFile.getCombinedData());
		burner.setFile(nesFile);
	}
	
	public void initializeBurnSequence() {
		burner.startFullBurnSequence();
	}
	
	public void initializeReadSequence() {
		burner.startReadAndCompareSequence();
	}
	
	public void changeChip(String chip) {
		burner.changeChip(chip);
	}

	public SerialPortManager getSerialManager() {
		return serialManager;
	}

}
