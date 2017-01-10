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

public class MessagePanel extends JPanel implements Notifiable {
	
	JScrollPane panelScroll;
	JTextArea messageArea;
	
	public MessagePanel(int width, int height) {
		super();
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		initialize(width, height);
	}
	
	private void initialize(int width, int height) {
		messageArea = new JTextArea();
		messageArea.setLineWrap(true);
		panelScroll = new JScrollPane(messageArea);
		panelScroll.setPreferredSize(new Dimension(width, height));
		panelScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(panelScroll);
	}

	@Override
	public void takeNotice(Object notifier, String message) {
		if(notifier instanceof BitBurner) {
			if(message.startsWith(BitBurner.updateMessageAreaChar)) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						messageArea.setText(messageArea.getText() + "\n" + message.substring(1));
					}
				});
				
			}
		}
		
	}

}
