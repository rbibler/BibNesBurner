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
	private int[] messageBuffer = new int[0xFF];
	private int messageIndex;
	private int serialState;
	
	private final int WAIT_HEADING = 0x02;
	private final int IN_MSG = 0x03;
	private final int AFTER_ESC = 0x04;
	
	private final int START_BYTE = 0x67;
	private final int STOP_BYTE = 0x68;
	private final int ESC = 0x6C;
	
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
				read = in.read();
				processNewCharacter(read);
			} while(read != -1 && in.available() > 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processNewCharacter(int newChar) {
		if(newChar < 0x10) {
			System.out.print("0");
		}
		System.out.print(Integer.toHexString(newChar & 0xFF) + " ");
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
				System.out.print("Escaping! ");
				serialState = AFTER_ESC;
			} else {
				addToMessage(newChar);
			}
			break;
		case AFTER_ESC:
				addToMessage(newChar);
				serialState = IN_MSG;
			break;
		}
	}
	
	private void processMessage() {
		System.out.println(Integer.toHexString(messageIndex));
		serialState = WAIT_HEADING;
		notifyAllObjects(messageBuffer, "" + messageIndex);
		messageIndex = 0;
	}
	
	private void addToMessage(int charToAdd) {
		messageBuffer[messageIndex++] = charToAdd;
	}

	@Override
	public void notifyAllObjects(Object packet, String message) {
		for(Notifiable notifiable : objectsToNotify) {
			notifiable.takeNotice(this, packet, message);
		}
		
	}
	
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			try {
				if(in.available() > 0) {	
					handleSerialInput();
				}
			} catch (IOException e) {}
		}	
	}

	public void writeInstruction(int instruction) {
		try {
			out.write(START_BYTE);
			out.write(instruction);
			out.write(STOP_BYTE);
			out.flush();
		} catch(IOException e) {}
	}
	
	public void writeBlock(int[] block, int startOffset, int length) {
		int outByte;
		try {
			out.write(START_BYTE);
			for(int i = startOffset; i < startOffset + length; i++) {
				outByte = (block[i] & 0xFF);
				if(outByte == START_BYTE || outByte == STOP_BYTE || outByte == ESC) {
					out.write(ESC);
				}
				out.write(outByte);
			}
			out.write(STOP_BYTE);
		} catch(IOException e) {}
	}

}
