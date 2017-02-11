package com.bibler.awesome.bibnesburner.fileutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import com.bibler.awesome.bibnesburner.interfaces.Notifiable;
import com.bibler.awesome.bibnesburner.interfaces.Notifier;

public class FileLoader implements Notifier {
	
	private JFileChooser chooser;
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();
	private String fileType;
	
	public FileLoader() {
		setupFileLoader();
	}
	
	public void registerObjectToNotify(Notifiable objectToNotify) {
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
	}
	
	public FileInputStream loadFile() {
		File f = openFile();
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(f);
		} catch(IOException e) {}
		return stream;
	}
	
	public void setupFileLoader() {
		Runnable chooserSetupTask = new Runnable() {
			@Override
			public void run() {
				chooser = new JFileChooser(new File("C:/users/ryan/desktop/nes/roms"));
			}
		};
		Thread t = new Thread(chooserSetupTask);
		t.run();
		
	}
	
	public boolean getBin() {
		return fileType.endsWith(".bin");
	}
	
	private File openFile() {
		int returnVal = chooser.showOpenDialog(null);
		File f = null;
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			f = chooser.getSelectedFile();
			fileType = f.getName();
			notifyAllObjects(this, f.getName());
		} 
		return f;
	}

	@Override
	public void notifyAllObjects(Object packet, String message) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(this, packet, message);
		}
		
	}

}
