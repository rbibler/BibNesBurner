package com.bibler.awesome.bibnesburner.serialutils;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import gnu.io.SerialPort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import com.bibler.awesome.bibnesburner.interfaces.Notifiable;
import com.bibler.awesome.bibnesburner.interfaces.Notifier;

/**
 *
 * @author mario
 */
public class SerialPortInstance implements Runnable, Notifier{

	private InputStream in = null;
	private OutputStream out = null;
	private SerialPort serialPort = null;
	private BufferedWriter serialWriter = null;
	private BufferedReader serialReader = null;
	private int[] messageBuffer = new int[0xFF];
	private int messageIndex;
	private int serialState;
	
	private final int WAIT_HEADING = 0x02;
	private final int IN_MSG = 0x03;
	private final int AFTER_ESC = 0x04;
	
	private final int START_BYTE = 0x7D;
	private final int STOP_BYTE = 0x7E;
	private final int SUCCESS = 0x8E;
	private final int FAILURE = 0x9C;
	private final int ESC = 0x12;
	
	private ArrayList<Notifiable> objectsToNotify = new ArrayList<Notifiable>();

	public SerialPortInstance(SerialPort serialPort) {
		in = null;
	    out = null;
	    this.serialPort = serialPort;
	    setupStreamsAndBuffers();
	    serialState = WAIT_HEADING;
	    Thread t = new Thread(this);
	    t.start();
	}
	
	private void setupStreamsAndBuffers() {
		try {
			in = serialPort.getInputStream();
			out = serialPort.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        serialReader = new BufferedReader(new InputStreamReader(in));
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
	
	private void handleSerialInput() {
		int read;
		try {
			do {
				read = serialReader.read();
				processNewCharacter(read);
			} while(read != -1 && serialReader.ready());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processNewCharacter(int newChar) {
		switch(serialState) {
		case WAIT_HEADING:
			
			if(newChar == START_BYTE) {
				serialState = IN_MSG;
			}
			break;
		case IN_MSG:
			if(newChar == STOP_BYTE) {
				processMessage();
			} else if(newChar == ESC) {
				serialState = AFTER_ESC;
			} else {
				addToMessage(newChar);
			}
			break;
		case AFTER_ESC:
			if(newChar == STOP_BYTE) {
				processMessage();
			} else {
				addToMessage(newChar);
			}
			break;
		}
	}
	
	private void processMessage() {
		serialState = WAIT_HEADING;
		for(int i = 0; i < messageIndex; i++) {
			System.out.print((char) messageBuffer[i]);
		}
	}
	
	private void addToMessage(int charToAdd) {
		messageBuffer[messageIndex++] = charToAdd;
	}

	@Override
	public void notifyAllObjects(String message) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(this, message);
		}
		
	}
	
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			try {
				if(serialReader.ready()) {	
					handleSerialInput();
				}
			} catch (IOException e) {}
		}	
	}

}
