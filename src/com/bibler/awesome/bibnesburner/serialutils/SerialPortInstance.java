package com.bibler.awesome.bibnesburner.serialutils;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;

import com.bibler.awesome.bibnesburner.interfaces.Notifiable;
import com.bibler.awesome.bibnesburner.interfaces.Notifier;

/**
 *
 * @author mario
 */
public class SerialPortInstance implements Notifier, Runnable {

	private InputStream in = null;
	private OutputStream out = null;
	private SerialPort serialPort = null;
	private BufferedWriter serialWriter = null;
	private BufferedReader serialReader = null;
	private boolean running;
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();

	public SerialPortInstance() {
		in = null;
	    out = null;
	    serialPort = null;
	}
	    
	public InputStream getSerialIn() {
	    return in;
	}
	    
	public OutputStream getSerialOut() {
		return out;
	}
	
	public void registerObjectToNotify(Notifiable objectToNotify) {
		if(!objectsToNotify.contains(objectToNotify)) {
			objectsToNotify.add(objectToNotify);
		}
	}
	
	public void writeString(String stringToWrite) {
		if(serialWriter == null) {
			setupSerialWriter();
		}
		try {
			serialWriter.write(stringToWrite);
			serialWriter.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setupSerialWriter() {
		serialWriter = new BufferedWriter(new OutputStreamWriter(out));
	}
	
	
    

    

	@Override
	public void notifyAllObjects(String message) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(this, message);
		}
		
	}

	@Override
	public void run() {
		while(running) {
			try {
				if(serialReader.ready()) {	
					String message = serialReader.readLine();
					notifyAllObjects(message);
				}
			} catch (IOException e) {}
		}	
	}
}
