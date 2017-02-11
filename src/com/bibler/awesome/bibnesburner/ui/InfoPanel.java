package com.bibler.awesome.bibnesburner.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.bibler.awesome.bibnesburner.burnerutils.BitBurner;
import com.bibler.awesome.bibnesburner.fileutils.FileLoader;
import com.bibler.awesome.bibnesburner.fileutils.NESFile;
import com.bibler.awesome.bibnesburner.interfaces.Notifiable;

public class InfoPanel extends JPanel implements Notifiable {
	
	ProgressPanel progressBar;
	JLabel fileName;
	JLabel chrSize;
	JLabel prgSize;
	JLabel timeElapsed;
	
	GridBagLayout layout = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	
	public InfoPanel(int width, int height) {
		super();
		setPreferredSize(new Dimension(width, height));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		initialize(width, height);
	}
	
	public void setFileName(String f) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fileName.setText("File: " + f);
			}
		});
		
	}
	
	public void setChrSize(int size) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				chrSize.setText("CHR Bytes: " + size);
			}
		});
		
	}
	
	public void setPrgSize(int size) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				prgSize.setText("PRG Bytes: " + size);
			}
		});
		
	}
	
	private void initialize(int width, int height) {
		setLayout(layout);
		fileName = new JLabel("File: ");
		chrSize = new JLabel("CHR Bytes: ");
		prgSize = new JLabel("PRG Bytes: ");
		timeElapsed = new JLabel("Time Elapsed: 0:00");
		progressBar = new ProgressPanel((int) (width * .5f), (int) (height * 75f));
		progressBar.updateProgress(.75f);
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = .20;
		add(fileName, c);
		c.gridx = 1;
		c.weightx = .15;
		add(chrSize, c);
		c.gridx = 2;
		add(prgSize, c);
		c.weightx = .2;
		c.gridx = 3;
		c.gridwidth = 1;
		add(timeElapsed, c);
		c.gridx = 4;
		c.gridwidth = 3;
		c.weightx = .5;
		c.anchor = GridBagConstraints.LINE_END;
		add(progressBar, c);
		
	}
	
	private class ProgressPanel extends JPanel {
		float percentFull;
		
		int width;
		int height;
		
		public ProgressPanel(int width, int height) {
			super();
			this.width = width;
			this.height = height;
			setPreferredSize(new Dimension(width, height));
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
		public void updateProgress(float percentFull) {
			this.percentFull = percentFull;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					
					repaint();
				}
			});
			
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, (int) (this.getWidth() * percentFull), height);
		}
	}

	@Override
	public void takeNotice(Object notifier, Object packet, String message) {
		if(notifier instanceof FileLoader) {
			setFileName(message);
		} else if(notifier instanceof NESFile) {
			if(message.startsWith("C")) {
				setChrSize(Integer.parseInt(message.substring(1)));
			} else if(message.startsWith("P")) {
				setPrgSize(Integer.parseInt(message.substring(1)));
			}
		} else if(notifier instanceof BitBurner) {
			if(message.startsWith(BitBurner.updateProgressBarChar)) {
				float f = Float.parseFloat(message.substring(1));
				progressBar.updateProgress(f);
			}
		}	
	}
}
