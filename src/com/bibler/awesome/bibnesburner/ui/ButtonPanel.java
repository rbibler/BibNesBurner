package com.bibler.awesome.bibnesburner.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.bibler.awesome.bibnesburner.burnerutils.BitBurner;

import gnu.io.CommPortIdentifier;

public class ButtonPanel extends JPanel {
	
	private JButton loadButton;
	private JButton readButton;
	private JButton burnPrgButton;
	private JButton burnChrButton;
	private JComboBox<String> chipChooserBox;
	private JComboBox<String> serialChooserBox;
	private MainFrame parentFrame;
	
	public ButtonPanel(int width, int height, MainFrame parentFrame) {
		super();
		this.parentFrame = parentFrame;
		setPreferredSize(new Dimension(width, height));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		initialize(parentFrame.getSerialManager().getAvailableSerialPortNames());
		initializeButtonClicks();
	}
	
	private void initialize(String[] ports) {
		loadButton = new JButton("Load Rom");
		burnPrgButton = new JButton("Burn PRG");
		burnChrButton = new JButton("Burg CHR");
		readButton = new JButton("Read Rom");
		chipChooserBox = new JComboBox<String>(new String[] {"AT28C256", "GLS29EE010", "AM29F040"});
		chipChooserBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String s = (String) chipChooserBox.getSelectedItem();
				parentFrame.changeChip(s);
			}
			
		});
		serialChooserBox = new JComboBox<String>(ports);
		serialChooserBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String s = (String) serialChooserBox.getSelectedItem();
				try {
					parentFrame.connectSerial(s);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		add(burnPrgButton, c);
		
		c.gridy = 1;
		add(burnChrButton, c);
		
		c.gridx = 0;
		add(readButton, c);
		
		c.gridy = 0;
		c.gridx = 3;
		c.weightx = 1;
		c.gridwidth = 2;
		c.insets = new Insets(0, 12, 12, 0);
		add(chipChooserBox, c);
		
		c.gridy = 1;
		add(serialChooserBox, c);
	}
	
	public void initializeButtonClicks() {
		ButtonClickListener listener = new ButtonClickListener();
		loadButton.setActionCommand("LOAD");
		burnPrgButton.setActionCommand("BURN_PRG");
		burnChrButton.setActionCommand("BURN_CHR");
		readButton.setActionCommand("READ");
		loadButton.addActionListener(listener);
		burnPrgButton.addActionListener(listener);
		burnChrButton.addActionListener(listener);
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
			case "BURN_PRG":
				parentFrame.initializeBurnSequence(BitBurner.PRG_BURN);
				break;
			case "BURN_CHR":
				parentFrame.initializeBurnSequence(BitBurner.CHR_BURN);
				break;
			case "READ":
				parentFrame.readFullRom();
				break;
		
			}
			
		}
		
	}

}
