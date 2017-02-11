package com.bibler.awesome.bibnesburner.ui;

import java.awt.Color;
import java.io.FileInputStream;

import javax.swing.JFrame;

import com.bibler.awesome.bibnesburner.burnerutils.BitBurner;
import com.bibler.awesome.bibnesburner.fileutils.FileLoader;
import com.bibler.awesome.bibnesburner.fileutils.NESFile;
import com.bibler.awesome.bibnesburner.serialutils.SerialPortInstance;
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
		burner = new BitBurner(this);
		loader = new FileLoader();
		panel = new MainPanel(this);
		loader.registerObjectToNotify(panel.getInfoPanel());
		burner.registerObjectToNotify(panel.getInfoPanel());
		burner.registerObjectToNotify(panel.getMessagePanel());
		add(panel);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		changeChip("AT28C256");
	}
	
	public void loadNewFile() {
		FileInputStream f = loader.loadFile();
		NESFile nesFile = new NESFile(f, panel.getInfoPanel(), loader.getBin());
		panel.getRomPanel().setHexData(nesFile.getCombinedData());
		burner.setFile(nesFile);
	}
	
	public void initializeBurnSequence(int thingToBurn) {
		burner.startFullBurnSequence(thingToBurn);
	}
	
	public void readFullRom() {
		burner.readFullRom();
	}
	
	public void changeChip(String chip) {
		burner.changeChip(chip);
	}

	public SerialPortManager getSerialManager() {
		return serialManager;
	}

	public void connectSerial(String s) {
		try {
			serialManager.connect(s, 57600);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SerialPortInstance port = serialManager.getActiveSerialPort();
		if(port != null) {
			burner.setSerialPort(port);
		} else {
			System.out.println("NO PORT!");
		}
	}

	public void setReadData(int[] dataIn) {
		panel.getReadPanel().setHexData(dataIn);
	}
	
	public void setRomData(int[] data) {
		panel.getRomPanel().setHexData(data);
		
	}
	
	public void colorizeRomValues(int startAddress, int length, Color color, Color bgColor) {
		panel.getRomPanel().colorizeValues(startAddress, length, color, bgColor);
	}
	
	public void colorizeReadValues(int startAddress, int length, Color color, Color bgColor) {
		panel.getReadPanel().colorizeValues(startAddress, length, color, bgColor);
	}

}
