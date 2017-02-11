package com.bibler.awesome.bibnesburner.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

public class MainPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6232998736607587607L;
	private MessagePanel messagePanel;
	private ButtonPanel buttonPanel;
	private InfoPanel infoPanel;
	private HexPanel romPanel;
	private HexPanel readPanel;
	
	private JPanel hexPanel;
	private JPanel middlePanel;
	
	private final int width = 800;
	private final int height = 900;
	
	private final float buttonPanelHeightPercent = .1f;
	private final float messagePanelHeightPercent = .85f;
	private final float messagePanelWidthPercent = .33f;
	private final float hexPanelHeightPercent = .85f;
	private final float hexPanelWidthPercent = .66f;
	private final float infoPanelHeightPercent = .05f;
	
	public MainPanel(MainFrame frame) {
		super();
		//setPreferredSize(new Dimension(width, height));
		setLayout(new BorderLayout());
		initializeButtonPanel(frame);
		initializeMessagePanel();
		initializeHexPanel();
		initializeInfoPanel();
		middlePanel = new JPanel();
		middlePanel.setLayout(new BorderLayout());
		middlePanel.add(messagePanel, BorderLayout.WEST);
		middlePanel.add(hexPanel, BorderLayout.EAST);
		add(buttonPanel, BorderLayout.NORTH);
		add(middlePanel, BorderLayout.CENTER);
		add(infoPanel, BorderLayout.SOUTH);
		
	}
	
	public InfoPanel getInfoPanel() {
		return infoPanel;
	}
	
	public MessagePanel getMessagePanel() {
		return messagePanel;
	}
	
	public HexPanel getRomPanel() {
		return romPanel;
	}
	
	public HexPanel getReadPanel() {
		return readPanel;
	}
	
	private void initializeButtonPanel(MainFrame frame) {
		buttonPanel = new ButtonPanel(width, (int) (height * buttonPanelHeightPercent), frame);
	}
	
	private void initializeMessagePanel() {
		messagePanel = new MessagePanel((int) (width * messagePanelWidthPercent), (int) (height * messagePanelHeightPercent));
	}
	
	private void initializeHexPanel() {
		romPanel = new HexPanel();
		readPanel = new HexPanel();
		JScrollPane romPane = new JScrollPane(romPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane readPane = new JScrollPane(readPanel);
		romPane.getVerticalScrollBar().setModel(readPane.getVerticalScrollBar().getModel());
		
		readPane.setPreferredSize(new Dimension((int) (width * hexPanelWidthPercent), (int) (height * hexPanelHeightPercent)));
		romPane.setPreferredSize(new Dimension((int) (width * hexPanelWidthPercent), (int) (height * hexPanelHeightPercent)));
		
		hexPanel = new JPanel();
		hexPanel.setLayout(new BorderLayout());
		hexPanel.add(romPane, BorderLayout.WEST);
		hexPanel.add(readPane, BorderLayout.EAST);
	}
	
	private void initializeInfoPanel() {
		infoPanel = new InfoPanel(width, (int) (height * infoPanelHeightPercent));
	}
	

}
