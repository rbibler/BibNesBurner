package com.bibler.awesome.bibnesburner.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class MainPanel extends JPanel {
	
	private GridBagLayout layout = new GridBagLayout();
	private GridBagConstraints c = new GridBagConstraints();
	private MessagePanel messagePanel;
	private ButtonPanel buttonPanel;
	private InfoPanel infoPanel;
	private HexPanel hexPanel;
	
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
		setLayout(layout);
		initializeButtonPanel(frame);
		initializeMessagePanel();
		initializeHexPanel();
		initializeInfoPanel();
		
	}
	
	public InfoPanel getInfoPanel() {
		return infoPanel;
	}
	
	public MessagePanel getMessagePanel() {
		return messagePanel;
	}
	
	public HexPanel getHexPanel() {
		return hexPanel;
	}
	
	private void initializeButtonPanel(MainFrame frame) {
		buttonPanel = new ButtonPanel(width, (int) (height * buttonPanelHeightPercent), frame);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = .1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		add(buttonPanel, c);
	}
	
	private void initializeMessagePanel() {
		messagePanel = new MessagePanel((int) (width * messagePanelWidthPercent), (int) (height * messagePanelHeightPercent));
		c.gridy = 1;
		c.weightx = .33;
		c.weighty = .85;
		c.gridwidth = 1;
		c.gridheight = 3;
		add(messagePanel, c);
	}
	
	private void initializeHexPanel() {
		hexPanel = new HexPanel((int) (width * hexPanelWidthPercent), (int) (height * hexPanelHeightPercent));
		c.gridx = 1;
		c.weightx = .66;
		c.gridheight = 3;
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(hexPanel, c);
	}
	
	private void initializeInfoPanel() {
		infoPanel = new InfoPanel(width, (int) (height * infoPanelHeightPercent));
		c.gridx = 0;
		c.gridy = 5;
		c.weightx = 1;
		c.weighty = .05;
		c.gridheight = 1;
		add(infoPanel, c);
	}
	

}
