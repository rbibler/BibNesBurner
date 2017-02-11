package com.bibler.awesome.bibnesburner.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.bibler.awesome.bibnesburner.burnerutils.BitBurner;
import com.bibler.awesome.bibnesburner.interfaces.Notifiable;

import tv.porst.jhexview.JHexView;
import tv.porst.jhexview.JHexView.DefinitionStatus;
import tv.porst.jhexview.SimpleDataProvider;

public class HexPanel extends JPanel  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4077584869548116559L;
	JHexView view;
	
	public HexPanel() {
		super();
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		initialize();
	}
	
	private void initialize() {
		view = new JHexView();
		setLayout(new BorderLayout());
		add(view, BorderLayout.CENTER);
	}
	
	public void setHexData(int[] data) {
		view.setData(new SimpleDataProvider(data));
		view.setDefinitionStatus(DefinitionStatus.DEFINED);
		view.setEnabled(true);
		view.setBytesPerColumn(1);
		view.repaint();
	}

	public void colorizeValues(int startAddress, int length, Color color, Color bgColor) {
		view.colorize(1, startAddress, length, color, bgColor);
	}
	
}
