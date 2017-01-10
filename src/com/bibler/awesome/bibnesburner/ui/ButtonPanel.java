package com.bibler.awesome.bibnesburner.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class ButtonPanel extends JPanel {
	
	private JButton loadButton;
	private JButton readButton;
	private JButton writeButton;
	private JComboBox<String> serialChooserBox;
	private MainFrame parentFrame;
	
	public ButtonPanel(int width, int height, MainFrame parentFrame) {
		super();
		this.parentFrame = parentFrame;
		setPreferredSize(new Dimension(width, height));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		initialize();
		initializeButtonClicks();
	}
	
	private void initialize() {
		loadButton = new JButton("Load Rom");
		writeButton = new JButton("Burn Rom");
		readButton = new JButton("Read Rom");
		serialChooserBox = new JComboBox<String>(new String[] {"AT28C256", "GLS29EE010", "AM29F040"});
		serialChooserBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String s = (String) serialChooserBox.getSelectedItem();
				parentFrame.changeChip(s);
			}
			
		});
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 1;
		c.weightx = .25;
		c.ipadx = 5;
		c.insets = new Insets(0, 12, 12, 0);
		c.fill = GridBagConstraints.BOTH;
		add(loadButton, c);
		c.gridx = 1;
		add(writeButton, c);
		c.gridx = 2;
		add(readButton, c);
		c.gridx = 3;
		c.weightx = 1;
		c.gridwidth = 2;
		c.insets = new Insets(0, 12, 12, 0);
		add(serialChooserBox, c);
	}
	
	public void initializeButtonClicks() {
		ButtonClickListener listener = new ButtonClickListener();
		loadButton.setActionCommand("LOAD");
		writeButton.setActionCommand("BURN");
		readButton.setActionCommand("READ");
		loadButton.addActionListener(listener);
		writeButton.addActionListener(listener);
		readButton.addActionListener(listener);
	}
	
	private class ButtonClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String command = arg0.getActionCommand();
			switch(command) {
			case "LOAD":
				parentFrame.loadNewFile();
				break;
			case "BURN":
				parentFrame.initializeBurnSequence();
				break;
			case "READ":
				parentFrame.initializeReadSequence();
				break;
		
			}
			
		}
		
	}

}
