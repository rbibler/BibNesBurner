package com.bibler.awesome.bibnesburner.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.bibler.awesome.bibnesburner.burnerutils.BitBurner;
import com.bibler.awesome.bibnesburner.interfaces.Notifiable;

public class HexPanel extends JPanel implements Notifiable {
	
	JScrollPane panelScroll;
	JTextArea panelText;
	
	public HexPanel(int width, int height) {
		super();
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		initialize(width, height);
	}
	
	private void initialize(int width, int height) {
		panelText = new JTextArea();
		panelText.setLineWrap(true);
		panelScroll = new JScrollPane(panelText);
		panelScroll.setPreferredSize(new Dimension(width, height));
		panelScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(panelScroll);
	}
	
	@Override
	public void takeNotice(Object notifier, String message) {
		if(notifier instanceof BitBurner) {
			if(message.startsWith(BitBurner.updateHexAreaChar)) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						panelText.setText(panelText.getText() + "\n" + message.substring(1));
					}
				});
				
			}
		}
		
	}

}
