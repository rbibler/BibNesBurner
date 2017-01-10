package com.bibler.awesome.bibnesburner.ui;

import java.io.File;
import java.io.FileInputStream;

import javax.swing.JFrame;

import com.bibler.awesome.bibnesburner.burnerutils.BitBurner;
import com.bibler.awesome.bibnesburner.fileutils.FileLoader;
import com.bibler.awesome.bibnesburner.fileutils.NESFile;

public class MainFrame extends JFrame {
	
	private MainPanel panel;
	private BitBurner burner;
	private FileLoader loader;
	
	public MainFrame() {
		super();
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

}
