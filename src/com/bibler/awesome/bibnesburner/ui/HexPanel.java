package com.bibler.awesome.bibnesburner.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.bibler.awesome.bibnesburner.burnerutils.BitBurner;
import com.bibler.awesome.bibnesburner.interfaces.Notifiable;

import tv.porst.jhexview.JHexView;
import tv.porst.jhexview.JHexView.DefinitionStatus;
import tv.porst.jhexview.SimpleDataProvider;

public class HexPanel extends JPanel implements Notifiable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4077584869548116559L;
	JHexView view;
	JScrollPane scrollPane;
	
	public HexPanel(int width, int height) {
		super();
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		initialize(width, height);
	}
	
	private void initialize(int width, int height) {
		view = new JHexView();
		scrollPane = new JScrollPane(view);
		scrollPane.setPreferredSize(new Dimension(width, height));
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
	}
	
	public void setHexData(int[] data) {
		view.setData(new SimpleDataProvider(data));
		view.setDefinitionStatus(DefinitionStatus.DEFINED);
		view.setEnabled(true);
		view.setBytesPerColumn(1);
		view.repaint();
	}
	
	@Override
	public void takeNotice(Object notifier, String message) {
		if(notifier instanceof BitBurner) {
			if(message.startsWith(BitBurner.updateHexAreaChar)) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						//panelText.setText(panelText.getText() + "\n" + message.substring(1));
					}
				});
				
			}
		}
		
	}

}
